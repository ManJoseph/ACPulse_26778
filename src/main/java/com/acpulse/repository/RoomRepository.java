package com.acpulse.repository;

import com.acpulse.model.Room;
import org.springframework.data.domain.Page; // Added import
import org.springframework.data.domain.Pageable; // Added import
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
    List<Room> findByRoomNameContainingIgnoreCase(String roomName);
    Page<Room> findByRoomNameContainingIgnoreCase(String roomName, Pageable pageable);
    Page<Room> findByStatusAndRoomNameContainingIgnoreCase(Room.RoomStatus status, String roomName, Pageable pageable);
    Page<Room> findByStatus(Room.RoomStatus status, Pageable pageable);
    Page<Room> findAll(Pageable pageable);
}