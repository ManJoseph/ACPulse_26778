package com.acpulse.repository;

import com.acpulse.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUser_IdAndIsRead(Integer userId, Boolean isRead);
    long countByUser_IdAndIsRead(Integer userId, Boolean isRead);
}
