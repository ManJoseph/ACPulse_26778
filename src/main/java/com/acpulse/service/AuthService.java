package com.acpulse.service;

import com.acpulse.dto.request.LoginRequest;
import com.acpulse.dto.request.RegisterRequest;
import com.acpulse.dto.response.AuthResponse;
import com.acpulse.exception.BadRequestException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import com.acpulse.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private VerificationRequestRepository verificationRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Check if ID already exists
        if (userRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
            throw new BadRequestException("Identification number already registered");
        }

        // Log the received roleType
        System.out.println("Received roleType: " + request.getRoleType());

        // Log all roles from the database
        System.out.println("All roles in DB: " + roleRepository.findAll().stream().map(Role::getRoleName).toList());

        // Get role
        Role role = roleRepository.findByRoleName(request.getRoleType().toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid role type"));

        // Create user with hashed password
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setIdentificationNumber(request.getIdentificationNumber());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDepartment(request.getDepartment());
        user.setRole(role);

        // If the user is an admin, approve them automatically. Otherwise, set to pending.
        if ("ADMIN".equalsIgnoreCase(request.getRoleType())) {
            user.setStatus(User.UserStatus.APPROVED);
        } else {
            user.setStatus(User.UserStatus.PENDING);
        }

        // Set location if provided
        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId()).orElse(null);
            user.setLocation(location);
        }

        user = userRepository.save(user);

        // Create verification request ONLY if not an auto-approved admin
        if (user.getStatus() != User.UserStatus.APPROVED) {
            VerificationRequest verificationRequest = new VerificationRequest();
            verificationRequest.setUser(user);
            verificationRequest.setSubmittedId(request.getIdentificationNumber());
            verificationRequest.setRequestType(VerificationRequest.RequestType.valueOf(request.getRoleType().toUpperCase()));
            verificationRequestRepository.save(verificationRequest);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration submitted. Awaiting admin approval.");
        response.put("userId", user.getId());
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Get user from repository
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Check if approved
            if (user.getStatus() != User.UserStatus.APPROVED) {
                throw new BadRequestException("Account is " + user.getStatus().name().toLowerCase());
            }

            // Generate JWT token with the clean role name. The security filter will handle authority creation.
            String cleanRoleName = user.getRole().getRoleName();
            String token = jwtService.generateToken(user.getId(), user.getEmail(), cleanRoleName);

            // Return user info with the clean role name
            return new AuthResponse(
                    token,
                    cleanRoleName,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getStatus().name()
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid credentials");
        }
    }
}
