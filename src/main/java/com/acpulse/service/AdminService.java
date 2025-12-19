package com.acpulse.service;

import com.acpulse.exception.NotFoundException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private VerificationRequestRepository verificationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordResetRequestRepository passwordResetRequestRepository;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long pendingVerifications = verificationRequestRepository.countByStatus(VerificationRequest.Status.PENDING);
        long occupiedRooms = roomRepository.countByStatus(Room.RoomStatus.OCCUPIED);

        stats.put("totalUsers", totalUsers);
        stats.put("pendingVerifications", pendingVerifications);
        stats.put("occupiedRooms", occupiedRooms);
        stats.put("systemHealth", "Good"); // Placeholder value

        return stats;
    }

    public List<Map<String, Object>> getVerificationRequests(String status, String search) {
        VerificationRequest.Status requestStatus = status != null ?
                VerificationRequest.Status.valueOf(status.toUpperCase()) :
                VerificationRequest.Status.PENDING;

        List<VerificationRequest> requests;
        if (search != null && !search.trim().isEmpty()) {
            requests = verificationRequestRepository.searchByStatusAndQuery(requestStatus, search);
        } else {
            requests = verificationRequestRepository.findByStatus(requestStatus);
        }
        
        List<Map<String, Object>> responses = new ArrayList<>();

        for (VerificationRequest request : requests) {
            User user = request.getUser();
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("requestId", request.getId());
                response.put("userId", user.getId());
                response.put("userName", user.getName());
                response.put("email", user.getEmail());
                response.put("requestType", request.getRequestType().name());
                response.put("submittedId", request.getSubmittedId());
                response.put("phoneNumber", user.getPhoneNumber());
                response.put("department", user.getDepartment());
                response.put("status", request.getStatus().name());
                response.put("submittedAt", request.getSubmittedAt());

                if (user.getLocation() != null) {
                    response.put("locationId", user.getLocation().getId());
                    response.put("locationName", user.getLocation().getName());
                }

                responses.add(response);
            }
        }

        return responses;
    }

    @Transactional
    public Map<String, Object> approveUser(Integer requestId, Integer adminId) {
        VerificationRequest request = verificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Verification request not found"));

        User user = request.getUser();
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // Update verification request
        request.setStatus(VerificationRequest.Status.APPROVED);
        request.setReviewedBy(adminId);
        request.setReviewedAt(LocalDateTime.now());
        verificationRequestRepository.save(request);

        // Update user status
        user.setStatus(User.UserStatus.APPROVED);

        // Set user role based on request type
        String roleName = request.getRequestType().name(); // e.g., "LECTURER", "STUDENT", "STAFF"
        Role userRole = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found for type: " + roleName));
        user.setRole(userRole);

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Send notification
        notificationService.createNotification(
                user.getId(),
                "Account Approved!",
                "Your ACPulse account has been approved. You can now login.",
                Notification.NotificationType.APPROVAL
        );

        // Send email (async, don't block if it fails)
        try {
            emailService.sendApprovalEmail(user);
        } catch (Exception e) {
            // Log but don't fail
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User approved successfully");
        response.put("userId", user.getId());
        response.put("emailSent", true);
        return response;
    }

    @Transactional
    public Map<String, Object> rejectUser(Integer requestId, Integer adminId, String reason) {
        VerificationRequest request = verificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Verification request not found"));

        User user = request.getUser();
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // Update verification request
        request.setStatus(VerificationRequest.Status.REJECTED);
        request.setReviewedBy(adminId);
        request.setRejectionReason(reason);
        request.setReviewedAt(LocalDateTime.now());
        verificationRequestRepository.save(request);

        // Update user status
        user.setStatus(User.UserStatus.REJECTED);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Send notification
        notificationService.createNotification(
                user.getId(),
                "Account Registration - Action Required",
                "Your registration request has been reviewed. Reason: " + reason,
                Notification.NotificationType.REJECTION
        );

        // Send email (async, don't block if it fails)
        try {
            emailService.sendRejectionEmail(user, reason);
        } catch (Exception e) {
            // Log but don't fail
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User rejected");
        response.put("userId", user.getId());
        return response;
    }
    
    public Page<Map<String, Object>> getUsers(String search, String role, Pageable pageable) {
        Page<User> userPage;
        if (role != null && !role.isEmpty()) {
            userPage = userRepository.findByNameContainingIgnoreCaseAndRole_RoleNameIgnoreCase(search, role, pageable);
        } else {
            userPage = userRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return userPage.map(this::toUserResponse);
    }

    @Transactional
    public Map<String, Object> updateUser(Integer userId, Map<String, Object> updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName((String) value);
                    break;
                case "department":
                    user.setDepartment((String) value);
                    break;
                case "role":
                    Role newRole = roleRepository.findByRoleName((String) value)
                            .orElseThrow(() -> new NotFoundException("Role not found"));
                    user.setRole(newRole);
                    break;
                case "status":
                    user.setStatus(User.UserStatus.valueOf((String) value));
                    break;
            }
        });

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return toUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    private Map<String, Object> toUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getRoleName());
        response.put("department", user.getDepartment());
        response.put("status", user.getStatus().name());
        response.put("createdAt", user.getCreatedAt());
        response.put("updatedAt", user.getUpdatedAt());
        return response;
    }

    public List<Map<String, Object>> getPasswordResetRequests() {
        List<PasswordResetRequest> requests = passwordResetRequestRepository.findByStatus(PasswordResetRequest.RequestStatus.PENDING);
        List<Map<String, Object>> response = new ArrayList<>();
        for (PasswordResetRequest request : requests) {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("id", request.getId());
            reqMap.put("createdAt", request.getCreatedAt());
            
            User user = request.getUser();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            reqMap.put("user", userMap);
            
            response.add(reqMap);
        }
        return response;
    }

    @Transactional
    public void approvePasswordReset(Integer requestId, Integer adminId) {
        PasswordResetRequest request = passwordResetRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Password reset request not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin user not found"));

        if (request.getStatus() != PasswordResetRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been actioned.");
        }

        // Check if token has expired before approving
        if (LocalDateTime.now().isAfter(request.getExpiryDate())) {
            throw new com.acpulse.exception.BadRequestException("This reset request has expired. User must submit a new request.");
        }

        // Simply approve - token already exists from forgotPassword()
        request.setStatus(PasswordResetRequest.RequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(admin);
        passwordResetRequestRepository.save(request);

        // Send email with existing token
        emailService.sendPasswordResetEmail(request.getUser(), request.getToken());
    }

    @Transactional
    public Map<String, String> rejectPasswordReset(Integer requestId, Integer adminId, String reason) {
        PasswordResetRequest request = passwordResetRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Password reset request not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin user not found"));

        if (request.getStatus() != PasswordResetRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been actioned.");
        }

        // Reject the request
        request.setStatus(PasswordResetRequest.RequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(admin);
        passwordResetRequestRepository.save(request);

        // Send rejection email
        emailService.sendPasswordResetRejectionEmail(request.getUser(), reason);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset request rejected successfully");
        return response;
    }
}