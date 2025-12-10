package com.acpulse.service;

import com.acpulse.dto.response.NotificationResponse; // Import the new DTO
import com.acpulse.model.Notification;
import com.acpulse.model.User;
import com.acpulse.repository.NotificationRepository;
import com.acpulse.repository.UserRepository;
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
    private UserRepository userRepository;

    @Transactional
    public Notification createNotification(Integer userId, String title, String message,
                                           Notification.NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        return notificationRepository.save(notification);
    }

    public List<NotificationResponse> getUserNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByUser_IdAndIsRead(userId, false);
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationRepository.findByUser_IdAndIsRead(userId, false)
                .forEach(notification -> {
                    notification.setIsRead(true);
                    notification.setReadAt(LocalDateTime.now());
                });
    }

    @Transactional
    public void markAsRead(Integer notificationId, Integer userId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser() != null && notification.getUser().getId().equals(userId)) {
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
        });
    }
}
