package com.acpulse.service;

import com.acpulse.dto.request.ScheduleRequest;
import com.acpulse.dto.request.UpdateStatusRequest;
import com.acpulse.dto.response.LectureScheduleResponse; // Import the new DTO
import com.acpulse.dto.response.LecturerResponse; // Import the new DTO
import com.acpulse.exception.NotFoundException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Added import
import org.springframework.data.domain.Pageable; // Added import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LecturerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LecturerStatusRepository lecturerStatusRepository;

    @Autowired
    private LectureScheduleRepository lectureScheduleRepository; // New autowired repository

    @Autowired
    private RoomRepository roomRepository;

    public Page<LecturerResponse> getLecturers(String search, String status, Pageable pageable) {
        Page<User> lecturerPage;
        if (status != null && !status.trim().isEmpty()) {
            // Find lecturers with this active status
            try {
                LecturerStatus.Status statusEnum = LecturerStatus.Status.valueOf(status.toUpperCase());
                List<Integer> lecturerIds = lecturerStatusRepository.findByStatusAndIsActive(statusEnum, true)
                        .stream()
                        .map(ls -> ls.getLecturer().getId())
                        .collect(Collectors.toList());
                
                if (lecturerIds.isEmpty()) {
                    return Page.empty(pageable); // No lecturers with this status
                }

                if (search != null && !search.trim().isEmpty()) {
                     lecturerPage = userRepository.findByIdInAndNameContainingIgnoreCase(lecturerIds, search, pageable);
                } else {
                     lecturerPage = userRepository.findByIdIn(lecturerIds, pageable);
                }
            } catch (IllegalArgumentException e) {
                // Invalid status string, return empty or ignore? Let's return empty to be safe
                return Page.empty(pageable);
            }
        } else {
            if (search != null && !search.trim().isEmpty()) {
                lecturerPage = userRepository.findByNameContainingIgnoreCaseAndRole_RoleNameIgnoreCase(search, "LECTURER", pageable);
            } else {
                lecturerPage = userRepository.findByRole_RoleNameIgnoreCase("LECTURER", pageable);
            }
        }
        return lecturerPage.map(this::toLecturerResponse);
    }

    private LecturerResponse toLecturerResponse(User lecturer) {
        LecturerResponse response = new LecturerResponse();
        response.setId(lecturer.getId());
        response.setName(lecturer.getName());
        response.setEmail(lecturer.getEmail());
        response.setDepartment(lecturer.getDepartment());
        response.setPhoneNumber(lecturer.getPhoneNumber());

        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturer.getId(), true)
                .ifPresent(lecturerStatus -> {
                    response.setStatus(lecturerStatus.getStatus().name());
                    if (lecturerStatus.getCurrentRoom() != null) {
                        response.setOfficeName(lecturerStatus.getCurrentRoom().getRoomName());
                    }
                });
        return response;
    }

    public LecturerResponse getLecturerById(Integer id) {
        User lecturer = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + id));
        return toLecturerResponse(lecturer);
    }


    // New method: Search lecturers by name
    @Transactional(readOnly = true)
    public List<Map<String, Object>> searchLecturers(String query) {
        List<User> lecturers = userRepository.findByNameContainingIgnoreCaseAndRole_RoleName(query, "LECTURER");
        return lecturers.stream()
                .map(lecturer -> {
                    Map<String, Object> lecturerMap = new HashMap<>();
                    lecturerMap.put("id", lecturer.getId());
                    lecturerMap.put("name", lecturer.getName());
                    return lecturerMap;
                })
                .collect(Collectors.toList());
    }

    // New method: Get lecturer's current status
    @Transactional(readOnly = true)
    public Map<String, Object> getLecturerStatus(Integer lecturerId) {
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + lecturerId));

        Optional<LecturerStatus> activeStatusOpt = lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true);

        Map<String, Object> statusMap = new HashMap<>();
        if (activeStatusOpt.isPresent()) {
            LecturerStatus activeStatus = activeStatusOpt.get();
            statusMap.put("status", activeStatus.getStatus());
            statusMap.put("updatedAt", activeStatus.getStatusStartTime());
            statusMap.put("occupiedUntil", activeStatus.getExpectedEndTime());
            if (activeStatus.getCurrentRoom() != null) {
                statusMap.put("office", activeStatus.getCurrentRoom().getRoomName());
                statusMap.put("roomNumber", activeStatus.getCurrentRoom().getRoomNumber());
                statusMap.put("building", activeStatus.getCurrentRoom().getBuilding());
                statusMap.put("floor", activeStatus.getCurrentRoom().getFloor());
            }
        } else {
            statusMap.put("status", "UNKNOWN");
        }
        return statusMap;
    }

    // New method: Update lecturer's status
    @Transactional
    public Map<String, String> updateStatus(Integer lecturerId, UpdateStatusRequest request) {
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + lecturerId));

        // Deactivate current status and release room if applicable
        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true).ifPresent(oldStatus -> {
            // Release the room if one was occupied
            if (oldStatus.getCurrentRoom() != null) {
                Room room = oldStatus.getCurrentRoom();
                room.setOccupiedUntil(null);
                room.setCurrentLecturer(null);
                room.setStatus(Room.RoomStatus.AVAILABLE);
                room.setStatusUpdatedAt(LocalDateTime.now());
                roomRepository.save(room); // Explicitly save room changes
            }
            oldStatus.setIsActive(false);
            lecturerStatusRepository.save(oldStatus);
        });

        // Create new status
        LecturerStatus newStatus = new LecturerStatus();
        newStatus.setLecturer(lecturer);
        newStatus.setStatus(LecturerStatus.Status.valueOf(request.getStatus().toUpperCase()));
        newStatus.setCustomMessage(request.getCustomMessage());
        newStatus.setStatusStartTime(LocalDateTime.now());
        newStatus.setIsActive(true);

        lecturerStatusRepository.save(newStatus);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Lecturer status updated successfully.");
        return response;
    }

    // New method: Get lecturer's schedule
    public List<LectureScheduleResponse> getLecturerSchedule(Integer lecturerId) {
        // Validate if lecturer exists
        userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + lecturerId));
        
        List<LectureSchedule> schedules = lectureScheduleRepository.findByLecturer_IdOrderByDayOfWeekAscStartTimeAsc(lecturerId);
        
        // Handle null or empty list
        if (schedules == null || schedules.isEmpty()) {
            return new ArrayList<>();
        }
        
        return schedules.stream().map(schedule -> {
            LectureScheduleResponse dto = new LectureScheduleResponse();
            dto.setId(schedule.getId());
            dto.setCourseName(schedule.getCourseName() != null ? schedule.getCourseName() : "Untitled Class"); // Handle null
            dto.setDayOfWeek(schedule.getDayOfWeek());
            dto.setStartTime(schedule.getStartTime());
            dto.setEndTime(schedule.getEndTime());
            dto.setLecturerId(schedule.getLecturer().getId());
            dto.setLecturerName(schedule.getLecturer().getName());
            if (schedule.getRoom() != null) {
                dto.setRoomId(schedule.getRoom().getId());
                dto.setRoomName(schedule.getRoom().getRoomName());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // New method: Set/Update lecturer's schedule
    @Transactional
    public LectureScheduleResponse setLecturerSchedule(Integer lecturerId, ScheduleRequest scheduleRequest) {
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + lecturerId));
        
        LectureSchedule schedule;
        if (scheduleRequest.getId() != null) {
            // Update existing schedule entry
            schedule = lectureScheduleRepository.findById(scheduleRequest.getId())
                    .orElseThrow(() -> new NotFoundException("Schedule entry not found with id: " + scheduleRequest.getId()));
            if (!schedule.getLecturer().getId().equals(lecturerId)) {
                throw new SecurityException("Lecturer ID mismatch for schedule update.");
            }
            schedule.setCourseName(scheduleRequest.getCourseName());
            schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
            schedule.setStartTime(scheduleRequest.getStartTime());
            schedule.setEndTime(scheduleRequest.getEndTime());
            
            // Handle Room Assignment
            if (scheduleRequest.getRoomId() != null) {
                Room room = roomRepository.findById(scheduleRequest.getRoomId())
                        .orElseThrow(() -> new NotFoundException("Room not found with id: " + scheduleRequest.getRoomId()));
                schedule.setRoom(room);
            } else {
                schedule.setRoom(null);
            }

            schedule.setUpdatedAt(LocalDateTime.now());
        } else {
            // Create new schedule entry
            schedule = new LectureSchedule();
            schedule.setLecturer(lecturer);
            schedule.setCourseName(scheduleRequest.getCourseName());
            schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
            schedule.setStartTime(scheduleRequest.getStartTime());
            schedule.setEndTime(scheduleRequest.getEndTime());
            
            // Handle Room Assignment
            if (scheduleRequest.getRoomId() != null) {
                Room room = roomRepository.findById(scheduleRequest.getRoomId())
                        .orElseThrow(() -> new NotFoundException("Room not found with id: " + scheduleRequest.getRoomId()));
                schedule.setRoom(room);
            }
            // createdAt is set by default in model
        }
        
        LectureSchedule saved = lectureScheduleRepository.save(schedule);
        
        // Return DTO
        LectureScheduleResponse dto = new LectureScheduleResponse();
        dto.setId(saved.getId());
        dto.setCourseName(saved.getCourseName());
        dto.setDayOfWeek(saved.getDayOfWeek());
        dto.setStartTime(saved.getStartTime());
        dto.setEndTime(saved.getEndTime());
        dto.setLecturerId(saved.getLecturer().getId());
        dto.setLecturerName(saved.getLecturer().getName());
        return dto;
    }

    // New method: Delete a schedule entry
    @Transactional
    public void deleteLecturerScheduleEntry(Integer scheduleId) {
        if (!lectureScheduleRepository.existsById(scheduleId)) {
            throw new NotFoundException("Schedule entry not found with id: " + scheduleId);
        }
        lectureScheduleRepository.deleteById(scheduleId);
    }
}