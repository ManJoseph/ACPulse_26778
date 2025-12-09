package com.acpulse.controller;

import com.acpulse.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = adminService.getStats();
        return ResponseEntity.ok(stats);
    }

    //  Get all verification requests (filter by status)
    @GetMapping("/verification-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getVerificationRequests(
            @RequestParam(required = false, defaultValue = "PENDING") String status) {
        List<Map<String, Object>> response = adminService.getVerificationRequests(status);
        return ResponseEntity.ok(response);
    }

    //  Approve a user request
    @PostMapping("/verification-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> approveUser(
            @PathVariable Integer requestId,
            @RequestParam Integer adminId) {
        Map<String, Object> response = adminService.approveUser(requestId, adminId);
        return ResponseEntity.ok(response);
    }

    // Reject a user request
    @PostMapping("/verification-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> rejectUser(
            @PathVariable Integer requestId,
            @RequestParam Integer adminId,
            @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        Map<String, Object> response = adminService.rejectUser(requestId, adminId, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Map<String, Object>>> getUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> users = adminService.getUsers(search, role, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable("id") Integer userId,
            @RequestBody Map<String, Object> updates) {
        Map<String, Object> updatedUser = adminService.updateUser(userId, updates);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}