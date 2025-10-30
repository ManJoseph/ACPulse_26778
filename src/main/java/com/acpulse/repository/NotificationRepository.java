package com.acpulse.repository;

import com.acpulse.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUserIdAndIsRead(Integer userId, Boolean isRead);
    long countByUserIdAndIsRead(Integer userId, Boolean isRead);
}
