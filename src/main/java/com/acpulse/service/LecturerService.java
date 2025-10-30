package com.acpulse.service;

import com.acpulse.dto.request.UpdateStatusRequest;
import com.acpulse.exception.NotFoundException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class LecturerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LecturerStatusRepository lecturerStatusRepository;

    @Autowired
    private RoomRepository roomRepository;

    public List<Map<String, Object>> searchLecturers(String query) {
        List<User> lecturers = userRepository.findByNameContainingIgnoreCaseAndRole_RoleName(query, "LECTURER");
        List<Map<String, Object>> responses = new ArrayList<>();

        for (User lecturer : lecturers) {
            Map<String, Object> response = new HashMap<>();
            response.put("lecturerId", lecturer.getId());
            response.put("name", lecturer.getName());
            response.put("department", lecturer.getDepartment());
            response.put("email", lecturer.getEmail());
            response.put("phoneNumber", lecturer.getPhoneNumber());

            // Get current status
            lecturerStatusRepository.findByLecturerIdAndIsActive(lecturer.getId(), true)
                    .ifPresent(status -> {
                        response.put("currentStatus", status.getStatus().name());
                        response.put("statusMessage", status.getCustomMessage());
                        response.put("expectedAvailableTime", status.getExpectedEndTime());

                        // Add room info if teaching
                        if (status.getCurrentRoomId() != null) {
                            roomRepository.findById(status.getCurrentRoomId())
                                    .ifPresent(room -> {
                                        Map<String, Object> roomInfo = new HashMap<>();
                                        roomInfo.put("roomNumber", room.getRoomNumber());
                                        roomInfo.put("roomName", room.getRoomName());
                                        response.put("currentRoom", roomInfo);
                                    });
                        }
                    });

            responses.add(response);
        }

        return responses;
    }

    public Map<String, Object> getLecturerStatus(Integer lecturerId) {
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("lecturerId", lecturer.getId());

        lecturerStatusRepository.findByLecturerIdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    response.put("status", status.getStatus().name());
                    response.put("customMessage", status.getCustomMessage());
                    response.put("statusStartTime", status.getStatusStartTime());
                    response.put("expectedEndTime", status.getExpectedEndTime());
                    response.put("isActive", status.getIsActive());

                    // Add room info if present
                    if (status.getCurrentRoomId() != null) {
                        roomRepository.findById(status.getCurrentRoomId())
                                .ifPresent(room -> {
                                    Map<String, String> roomInfo = new HashMap<>();
                                    roomInfo.put("roomNumber", room.getRoomNumber());
                                    roomInfo.put("roomName", room.getRoomName());
                                    response.put("currentRoom", roomInfo);
                                });
                    }
                });

        return response;
    }

    @Transactional
    public Map<String, String> updateStatus(Integer lecturerId, UpdateStatusRequest request) {
        // Deactivate previous status
        lecturerStatusRepository.findByLecturerIdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    status.setIsActive(false);
                    lecturerStatusRepository.save(status);
                });

        // Create new status
        LecturerStatus status = new LecturerStatus();
        status.setLecturerId(lecturerId);
        status.setStatus(LecturerStatus.Status.valueOf(request.getStatus().toUpperCase()));
        status.setCustomMessage(request.getCustomMessage());
        lecturerStatusRepository.save(status);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Status updated successfully");
        return response;
    }
}
