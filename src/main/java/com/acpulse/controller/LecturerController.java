package com.acpulse.controller;

import com.acpulse.dto.request.UpdateStatusRequest;
import com.acpulse.service.LecturerService;
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
