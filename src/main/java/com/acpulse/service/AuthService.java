package com.acpulse.service;

import com.acpulse.dto.request.LoginRequest;
import com.acpulse.dto.request.RegisterRequest;
import com.acpulse.dto.response.AuthResponse;
import com.acpulse.exception.BadRequestException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {
        //if it is missing name
        if (request.getName().isEmpty()){
            throw new BadRequestException("Name is required");

        }
        //if the password is short
        if (request.getPassword().length() < 6) {
            throw new BadRequestException("Password too short");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Check if ID already exists
        if (userRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
            throw new BadRequestException("Identification number already registered");
        }

        // Get role
        Role role = roleRepository.findByRoleName(request.getRoleType().toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid role type"));

        // Create user (plain password for now)
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword()); // ⚠️ plain text for now
        user.setIdentificationNumber(request.getIdentificationNumber());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDepartment(request.getDepartment());
        user.setStatus(User.UserStatus.PENDING);
        user.setRole(role);

        // Set location if provided
        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId()).orElse(null);
            user.setLocation(location);
        }

        user = userRepository.save(user);

        // Create verification request
        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setUser(user);
        verificationRequest.setSubmittedId(request.getIdentificationNumber());
        verificationRequest.setRequestType(VerificationRequest.RequestType.valueOf(request.getRoleType().toUpperCase()));
        verificationRequestRepository.save(verificationRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration submitted. Awaiting admin approval.");
        response.put("userId", user.getId());
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        // Plain password comparison (for now)
        if (!request.getPassword().equals(user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        // Check if approved
        if (user.getStatus() != User.UserStatus.APPROVED) {
            throw new BadRequestException("Account is " + user.getStatus().name().toLowerCase());
        }

        // Return user info (no token)
        return new AuthResponse(
                null, // No token
                user.getRole().getRoleName(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus().name()
        );
    }
}
