package com.acpulse.repository;

import com.acpulse.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByStatus(Room.RoomStatus status);
    List<Room> findByStatusAndOccupiedUntilBefore(Room.RoomStatus status, LocalDateTime time);
    List<Room> findByCurrentLecturer_Id(Integer lecturerId);
    long countByStatus(Room.RoomStatus status);
}