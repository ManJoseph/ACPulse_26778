package com.acpulse.service;

import com.acpulse.dto.request.ScheduleRequest;
import com.acpulse.dto.request.UpdateStatusRequest;
import com.acpulse.dto.response.LecturerResponse; // Import the new DTO
import com.acpulse.exception.NotFoundException;
import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
                        }
                    } else {
                        matchesStatus = false; // No active status means no match for a specific status filter
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

    // New method: Get lecturer's schedule
    public List<LectureSchedule> getLecturerSchedule(Integer lecturerId) {
        // Validate if lecturer exists
        userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + lecturerId));
        
        return lectureScheduleRepository.findByLecturer_IdOrderByDayOfWeekAscStartTimeAsc(lecturerId);
    }

    // New method: Set/Update lecturer's schedule
    @Transactional
    public LectureSchedule setLecturerSchedule(Integer lecturerId, ScheduleRequest scheduleRequest) {
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
            schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
            schedule.setStartTime(scheduleRequest.getStartTime());
            schedule.setEndTime(scheduleRequest.getEndTime());
            schedule.setUpdatedAt(LocalDateTime.now());
        } else {
            // Create new schedule entry
            schedule = new LectureSchedule();
            schedule.setLecturer(lecturer);
            schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
            schedule.setStartTime(scheduleRequest.getStartTime());
            schedule.setEndTime(scheduleRequest.getEndTime());
            // createdAt is set by default in model
        }
        return lectureScheduleRepository.save(schedule);
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