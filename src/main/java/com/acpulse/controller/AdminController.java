package com.acpulse.controller;

import com.acpulse.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    //  Get all verification requests (filter by status)
    @GetMapping("/verification-requests")
    public ResponseEntity<List<Map<String, Object>>> getVerificationRequests(
            @RequestParam(required = false, defaultValue = "PENDING") String status) {
        List<Map<String, Object>> response = adminService.getVerificationRequests(status);
        return ResponseEntity.ok(response);
    }

    //  Approve a user request (pass adminId as query parameter) for every user does not have
    // to edit the user's status we need to pass an admin's Identity number
    @PostMapping("/verification-requests/{requestId}/approve")
    public ResponseEntity<Map<String, Object>> approveUser(
            @PathVariable Integer requestId,
            @RequestParam Integer adminId) {
        Map<String, Object> response = adminService.approveUser(requestId, adminId);
        return ResponseEntity.ok(response);
    }

    // Reject a user request (pass adminId as query parameter)
    @PostMapping("/verification-requests/{requestId}/reject")
    public ResponseEntity<Map<String, Object>> rejectUser(
            @PathVariable Integer requestId,
            @RequestParam Integer adminId,
            @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        Map<String, Object> response = adminService.rejectUser(requestId, adminId, reason);
        return ResponseEntity.ok(response);
    }
}
