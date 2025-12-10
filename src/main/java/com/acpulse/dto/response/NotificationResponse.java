package com.acpulse.dto.response;

import com.acpulse.model.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Integer id;
    private String title;
    private String message;
    private String notificationType;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Integer userId;
    private String userName; // Re-added userName

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.notificationType = notification.getNotificationType().name();
        this.isRead = notification.getIsRead();
        this.createdAt = notification.getCreatedAt();
        this.readAt = notification.getReadAt();
        if (notification.getUser() != null) { // Access User object
            this.userId = notification.getUser().getId();
            this.userName = notification.getUser().getName(); // Get userName from User object
        }
    }
}
