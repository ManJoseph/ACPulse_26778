package com.acpulse.service;

import com.acpulse.exception.NotFoundException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Map<String, Object>> getVerificationRequests(String status) {
        VerificationRequest.Status requestStatus = status != null ?
                VerificationRequest.Status.valueOf(status.toUpperCase()) :
                VerificationRequest.Status.PENDING;

        List<VerificationRequest> requests = verificationRequestRepository.findByStatus(requestStatus);
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
}
