package com.acpulse.controller;

import com.acpulse.exception.NotFoundException;
import com.acpulse.model.Office;
import com.acpulse.repository.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private OfficeRepository officeRepository;

    //  Fetch staff office by passing staffUserId as a query parameter
    @GetMapping("/office")
    public ResponseEntity<Office> getOffice(@RequestParam Integer staffUserId) {
        Office office = officeRepository.findByStaffUserId(staffUserId)
                .orElseThrow(() -> new NotFoundException("No office assigned to this staff member"));
        return ResponseEntity.ok(office);
    }

    //  Update office status using staffUserId as query parameter
    @PutMapping("/office/status")
    public ResponseEntity<Map<String, String>> updateOfficeStatus(
            @RequestParam Integer staffUserId,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        Office office = officeRepository.findByStaffUserId(staffUserId)
                .orElseThrow(() -> new NotFoundException("No office assigned to this staff member"));

        office.setAvailabilityStatus(Office.AvailabilityStatus.valueOf(status.toUpperCase()));
        office.setStatusUpdatedAt(LocalDateTime.now());
        officeRepository.save(office);

        return ResponseEntity.ok(Map.of("message", "Office status updated successfully"));
    }
}
