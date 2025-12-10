package com.acpulse.service;

import com.acpulse.dto.request.UpdateStatusRequest;
import com.acpulse.dto.response.LecturerResponse; // Import the new DTO
import com.acpulse.exception.NotFoundException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LecturerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LecturerStatusRepository lecturerStatusRepository;

    public List<LecturerResponse> getLecturers(String search, String status, int page, int size) {
        List<User> lecturers = userRepository.findByRole_RoleName("LECTURER");

        // Apply search and status filters in memory for now
        lecturers = lecturers.stream()
                .filter(lecturer -> {
                    boolean matchesSearch = true;
                    if (search != null && !search.trim().isEmpty()) {
                        String lowerCaseSearch = search.toLowerCase();
                        matchesSearch = lecturer.getName().toLowerCase().contains(lowerCaseSearch) ||
                                lecturer.getEmail().toLowerCase().contains(lowerCaseSearch) ||
                                (lecturer.getDepartment() != null && lecturer.getDepartment().toLowerCase().contains(lowerCaseSearch));
                    }
                    return matchesSearch;
                })
                .filter(lecturer -> {
                    boolean matchesStatus = true;
                    if (status != null && !status.trim().isEmpty()) {
                        // For simplicity in in-memory filtering, let's assume lecturerStatusRepository can give us the current status
                        // In a real scenario with proper JPA, this would be handled via joins or subqueries at the repository level.
                        Optional<LecturerStatus> currentStatus = lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturer.getId(), true);
                        if (currentStatus.isPresent()) {
                            matchesStatus = currentStatus.get().getStatus().name().equalsIgnoreCase(status);
                        } else {
                            matchesStatus = false; // No active status means no match for a specific status filter
                        }
                    }
                    return matchesStatus;
                })
                .collect(Collectors.toList());

        List<LecturerResponse> responses = new ArrayList<>();
        for (User lecturer : lecturers) {
            LecturerResponse response = new LecturerResponse();
            response.setId(lecturer.getId());
            response.setName(lecturer.getName());
            response.setEmail(lecturer.getEmail());
            response.setDepartment(lecturer.getDepartment());
            response.setPhoneNumber(lecturer.getPhoneNumber());

            // Get current status from LecturerStatusRepository
            lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturer.getId(), true)
                    .ifPresent(lecturerStatus -> {
                        response.setStatus(lecturerStatus.getStatus().name());
                        if (lecturerStatus.getCurrentRoom() != null) {
                            response.setOfficeName(lecturerStatus.getCurrentRoom().getRoomName());
                        }
                    });

            responses.add(response);
        }
        return responses;
    }


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
            lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturer.getId(), true)
                    .ifPresent(status -> {
                        response.put("currentStatus", status.getStatus().name());
                        response.put("statusMessage", status.getCustomMessage());
                        response.put("expectedAvailableTime", status.getExpectedEndTime());

                        // Add room info if teaching
                        if (status.getCurrentRoom() != null) {
                            Room room = status.getCurrentRoom();
                            Map<String, Object> roomInfo = new HashMap<>();
                            roomInfo.put("roomNumber", room.getRoomNumber());
                            roomInfo.put("roomName", room.getRoomName());
                            response.put("currentRoom", roomInfo);
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

        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    response.put("status", status.getStatus().name());
                    response.put("customMessage", status.getCustomMessage());
                    response.put("statusStartTime", status.getStatusStartTime());
                    response.put("expectedEndTime", status.getExpectedEndTime());
                    response.put("isActive", status.getIsActive());

                    // Add room info if present
                    if (status.getCurrentRoom() != null) {
                        Room room = status.getCurrentRoom();
                        Map<String, String> roomInfo = new HashMap<>();
                        roomInfo.put("roomNumber", room.getRoomNumber());
                        roomInfo.put("roomName", room.getRoomName());
                        response.put("currentRoom", roomInfo);
                    }
                });

        return response;
    }

    @Transactional
    public Map<String, String> updateStatus(Integer lecturerId, UpdateStatusRequest request) {
        // Get lecturer
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found"));

        // Deactivate previous status
        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    status.setIsActive(false);
                    lecturerStatusRepository.save(status);
                });

        // Create new status
        LecturerStatus status = new LecturerStatus();
        status.setLecturer(lecturer);
        status.setStatus(LecturerStatus.Status.valueOf(request.getStatus().toUpperCase()));
        status.setCustomMessage(request.getCustomMessage());
        lecturerStatusRepository.save(status);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Status updated successfully");
        return response;
    }
}