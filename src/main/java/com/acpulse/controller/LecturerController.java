package com.acpulse.controller;

import com.acpulse.dto.request.UpdateStatusRequest;
import com.acpulse.service.LecturerService;
import com.acpulse.service.LecturerService.LecturerResponse; // Import the new LecturerResponse DTO
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    @Autowired
    private LecturerService lecturerService;

    // Get all lecturers with optional filters and pagination
    @GetMapping
    public ResponseEntity<List<LecturerResponse>> getAllLecturers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<LecturerResponse> response = lecturerService.getLecturers(search, status, page, size);
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
}
