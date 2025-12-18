package com.acpulse.scheduler;

import com.acpulse.model.Notification;
import com.acpulse.model.Room;
import com.acpulse.repository.RoomRepository;
import com.acpulse.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingScheduler {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private NotificationService notificationService;

    // Run every minute
    @Scheduled(fixedRate = 60000)
    public void checkBookingExpirations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesFromNow = now.plusMinutes(5);
        LocalDateTime sixMinutesFromNow = now.plusMinutes(6);

        // Find rooms occupied ending between 5 and 6 minutes from now
        // This ensures we alert them roughly 5 mins before, without spamming every minute
        // Ideally we would have a 'notificationSent' flag, but for legacy support we use window
        
        List<Room> expiringRooms = roomRepository.findByStatusAndOccupiedUntilBetween(
                Room.RoomStatus.OCCUPIED,
                fiveMinutesFromNow,
                sixMinutesFromNow
        );

        for (Room room : expiringRooms) {
            if (room.getCurrentLecturer() != null) {
                notificationService.createNotification(
                        room.getCurrentLecturer().getId(),
                        "Booking Expiring Soon",
                        "Your booking for " + room.getRoomName() + " expires in 5 minutes.",
                        Notification.NotificationType.WARNING
                );
            }
        }
    }
}
