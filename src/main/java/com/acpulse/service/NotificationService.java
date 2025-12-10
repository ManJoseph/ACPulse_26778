package com.acpulse.service;

import com.acpulse.dto.response.NotificationResponse;
import com.acpulse.model.Notification;
import com.acpulse.model.User; // Keep User import for createNotification and markAsRead checks
import com.acpulse.repository.NotificationRepository;
import com.acpulse.repository.UserRepository; // Keep UserRepository for createNotification user check
import com.acpulse.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository; // Still needed to validate userId in createNotification

    @Transactional
    public Notification createNotification(Integer userId, String title, String message,
                                           Notification.NotificationType type) {
        // Validate if user exists before creating notification
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        Notification notification = new Notification();
        notification.setUserId(userId); // Set direct userId
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        return notificationRepository.save(notification);
    }

    public List<NotificationResponse> getUserNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId); // Use new method name
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false); // Use new method name
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationRepository.findByUserIdAndIsRead(userId, false) // Use new method name
                .forEach(notification -> {
                    notification.setIsRead(true);
                    notification.setReadAt(LocalDateTime.now());
                    notificationRepository.save(notification); // Explicitly save each modified notification
                });
    }

    @Transactional
    public void markAsRead(Integer notificationId, Integer userId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUserId().equals(userId)) { // Check direct userId
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
        });
    }
}
