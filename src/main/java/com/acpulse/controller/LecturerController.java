package com.acpulse.controller;

import com.acpulse.dto.request.ScheduleRequest;
import com.acpulse.dto.request.UpdateStatusRequest;
import com.acpulse.service.LecturerService;
import com.acpulse.dto.response.LecturerResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.acpulse.model.LectureSchedule;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    @Autowired
    private LecturerService lecturerService;

    // Get all lecturers with optional filters and pagination
    @GetMapping
    public ResponseEntity<Page<LecturerResponse>> getAllLecturers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LecturerResponse> response = lecturerService.getLecturers(search, status, pageable);
        return ResponseEntity.ok(response);
    }

    // Search lecturers by name or keyword
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchLecturers(@RequestParam String query) {
        List<Map<String, Object>> response = lecturerService.searchLecturers(query);
        return ResponseEntity.ok(response);
    }

    //  Get lecturer status (pass lecturerId as query parameter)
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(@RequestParam Integer lecturerId) {
        Map<String, Object> response = lecturerService.getLecturerStatus(lecturerId);
        return ResponseEntity.ok(response);
    }

    //  Update lecturer status
    @PutMapping("/status")
    public ResponseEntity<Map<String, String>> updateStatus(
            @RequestParam Integer lecturerId,
            @Valid @RequestBody UpdateStatusRequest request) {
        Map<String, String> response = lecturerService.updateStatus(lecturerId, request);
        return ResponseEntity.ok(response);
    }

    // Schedule Endpoints
    @GetMapping("/{lecturerId}/schedule")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')") // LECTURER can view their own, ADMIN can view all
    public ResponseEntity<List<LectureSchedule>> getLecturerSchedule(@PathVariable Integer lecturerId) {
        List<LectureSchedule> schedule = lecturerService.getLecturerSchedule(lecturerId);
        return ResponseEntity.ok(schedule);
    }

    @PostMapping("/{lecturerId}/schedule")
    @PreAuthorize("hasRole('LECTURER')") // Only LECTURER can set their own schedule
    public ResponseEntity<LectureSchedule> setLecturerSchedule(
            @PathVariable Integer lecturerId,
            @Valid @RequestBody ScheduleRequest request) {
        LectureSchedule schedule = lecturerService.setLecturerSchedule(lecturerId, request);
        return ResponseEntity.status(request.getId() == null ? 201 : 200).body(schedule); // 201 for create, 200 for update
    }

    @DeleteMapping("/schedule/{scheduleId}")
    @PreAuthorize("hasRole('LECTURER')") // Only LECTURER can delete their own schedule entry
    public ResponseEntity<Void> deleteLecturerScheduleEntry(@PathVariable Integer scheduleId) {
        lecturerService.deleteLecturerScheduleEntry(scheduleId);
        return ResponseEntity.noContent().build();
    }
}
