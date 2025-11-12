package com.acpulse.service;

import com.acpulse.dto.request.ExtendRoomRequest;
import com.acpulse.dto.request.OccupyRoomRequest;
import com.acpulse.dto.response.RoomResponse;
import com.acpulse.exception.BadRequestException;
import com.acpulse.exception.NotFoundException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LecturerStatusRepository lecturerStatusRepository;

    public RoomResponse searchRoom(String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomNumber));

        return buildRoomResponse(room);
    }

    public List<RoomResponse> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        List<RoomResponse> responses = new ArrayList<>();

        for (Room room : rooms) {
            responses.add(buildRoomResponse(room));
        }

        return responses;
    }

    @Transactional
    public Map<String, Object> occupyRoom(Integer lecturerId, OccupyRoomRequest request) {
        // Find room
        Room room = roomRepository.findByRoomNumber(request.getRoomNumber())
                .orElseThrow(() -> new NotFoundException("Room not found: " + request.getRoomNumber()));

        // Check if room is available
        if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
            throw new BadRequestException("Room is already occupied");
        }

        // Check if end time is in the future
        if (request.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("End time must be in the future");
        }

        // Get lecturer
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found"));

        // Update room
        room.setStatus(Room.RoomStatus.OCCUPIED);
        room.setCurrentLecturer(lecturer);
        room.setOccupiedUntil(request.getEndTime());
        room.setStatusUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);

        // Deactivate previous lecturer status
        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    status.setIsActive(false);
                    lecturerStatusRepository.save(status);
                });

        // Create new lecturer status
        LecturerStatus status = new LecturerStatus();
        status.setLecturer(lecturer);
        status.setStatus(LecturerStatus.Status.TEACHING);
        status.setCurrentRoom(room);
        status.setCustomMessage(request.getCustomMessage());
        status.setExpectedEndTime(request.getEndTime());
        status = lecturerStatusRepository.save(status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Room occupied successfully");
        response.put("roomId", room.getId());
        response.put("statusId", status.getId());
        response.put("occupiedUntil", request.getEndTime());
        return response;
    }

    @Transactional
    public Map<String, String> extendRoom(Integer lecturerId, Integer roomId, ExtendRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found"));

        // Verify lecturer is currently occupying this room
        if (room.getCurrentLecturer() == null || !lecturerId.equals(room.getCurrentLecturer().getId())) {
            throw new BadRequestException("You are not currently occupying this room");
        }

        // Check if new end time is in the future
        if (request.getNewEndTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("New end time must be in the future");
        }

        // Update room
        room.setOccupiedUntil(request.getNewEndTime());
        room.setStatusUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);

        // Update lecturer status
        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    status.setExpectedEndTime(request.getNewEndTime());
                    lecturerStatusRepository.save(status);
                });

        Map<String, String> response = new HashMap<>();
        response.put("message", "Room occupation extended until " + request.getNewEndTime());
        return response;
    }

    @Transactional
    public Map<String, String> releaseRoom(Integer lecturerId, Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found"));

        // Verify lecturer is currently occupying this room
        if (room.getCurrentLecturer() == null || !lecturerId.equals(room.getCurrentLecturer().getId())) {
            throw new BadRequestException("You are not currently occupying this room");
        }

        // Release room
        room.setStatus(Room.RoomStatus.AVAILABLE);
        room.setCurrentLecturer(null);
        room.setOccupiedUntil(null);
        room.setStatusUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);

        // Update lecturer status
        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    status.setStatus(LecturerStatus.Status.AVAILABLE);
                    status.setCurrentRoom(null);
                    status.setExpectedEndTime(null);
                    lecturerStatusRepository.save(status);
                });

        Map<String, String> response = new HashMap<>();
        response.put("message", "Room released successfully");
        return response;
    }

    private RoomResponse buildRoomResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomName(room.getRoomName());
        response.setBuilding(room.getBuilding());
        response.setFloor(room.getFloor());
        response.setCapacity(room.getCapacity());
        response.setRoomType(room.getRoomType().name());
        response.setStatus(room.getStatus().name());

        // Add lecturer info if occupied
        if (room.getStatus() == Room.RoomStatus.OCCUPIED && room.getCurrentLecturer() != null) {
            User lecturer = room.getCurrentLecturer();
            response.setCurrentLecturerName(lecturer.getName());

            response.setOccupiedUntil(room.getOccupiedUntil());
            response.setOccupiedSince(room.getStatusUpdatedAt());

            // Get occupation message
            lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturer.getId(), true)
                    .ifPresent(status -> response.setOccupationMessage(status.getCustomMessage()));
        }

        return response;
    }
}
