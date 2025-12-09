package com.acpulse.service;

import com.acpulse.model.Notification;
import com.acpulse.model.Room;
import com.acpulse.model.User;
import com.acpulse.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    @Transactional
    public void checkExpiredOccupations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gracePeriodEnd = now.minusMinutes(5);

        // Find expired rooms
        List<Room> expiredRooms = roomRepository.findByStatusAndOccupiedUntilBefore(
                Room.RoomStatus.OCCUPIED,
                gracePeriodEnd
        );

        for (Room room : expiredRooms) {
            if (room.getCurrentLecturer() != null) {
                User lecturer = room.getCurrentLecturer();
                // Send notification
                notificationService.createNotification(
                        lecturer.getId(),
                        "Room Occupation Expired",
                        "Your occupation of Room " + room.getRoomNumber() +
                                " expired at " + room.getOccupiedUntil() +
                                ". Please extend if still needed or release the room.",
                        Notification.NotificationType.ROOM_EXPIRY
                );
                
                // Send email notification
                try {
                    emailService.sendRoomExpiryNotification(lecturer, room.getRoomNumber());
                } catch (Exception e) {
                    // Log but don't fail - email sending is optional
                    System.err.println("Failed to send email notification: " + e.getMessage());
                }
            }
        }
    }
}
