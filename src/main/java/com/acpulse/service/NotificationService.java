package com.acpulse.service;

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

    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByUser_IdAndIsRead(userId, false);
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
