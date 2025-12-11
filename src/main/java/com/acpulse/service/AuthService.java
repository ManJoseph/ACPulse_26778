package com.acpulse.service;

import com.acpulse.dto.request.LoginRequest;
import com.acpulse.dto.request.RegisterRequest;
import com.acpulse.dto.request.VerifyOtpRequest;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetRequestRepository passwordResetRequestRepository;

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

    @Transactional
    public Map<String, Object> login(LoginRequest request) {
        try {
            // Authenticate user's credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Get user from repository
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Check if user account is approved
            if (user.getStatus() != User.UserStatus.APPROVED) {
                throw new BadRequestException("Account is " + user.getStatus().name().toLowerCase());
            }

            // Generate and save OTP
            String otp = generateOtp();
            user.setOtpCode(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);

            // Send OTP to user's email
            emailService.sendOtpEmail(user.getEmail(), otp);

            // Return pending status
            Map<String, Object> response = new HashMap<>();
            response.put("status", "PENDING_OTP");
            response.put("message", "OTP sent to email");
            return response;

        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid credentials");
        }
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new BadRequestException("OTP has expired");
        }

        // Clear OTP
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        // Generate JWT token
        String cleanRoleName = user.getRole().getRoleName();
        String token = jwtService.generateToken(user.getId(), user.getEmail(), cleanRoleName);

        // Return AuthResponse
        return new AuthResponse(
                token,
                cleanRoleName,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus().name()
        );
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setUser(user);
        resetRequest.setStatus(PasswordResetRequest.RequestStatus.PENDING);
        
        passwordResetRequestRepository.save(resetRequest);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetRequest request = passwordResetRequestRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token."));

        if (request.getStatus() != PasswordResetRequest.RequestStatus.APPROVED) {
            throw new BadRequestException("Password reset request not approved or already completed.");
        }

        if (LocalDateTime.now().isAfter(request.getExpiryDate())) {
            throw new BadRequestException("Password reset token has expired.");
        }

        User user = request.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        request.setStatus(PasswordResetRequest.RequestStatus.COMPLETED);
        passwordResetRequestRepository.save(request);
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}

