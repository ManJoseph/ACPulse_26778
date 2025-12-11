package com.acpulse.controller;

import com.acpulse.dto.request.ExtendRoomRequest;
import com.acpulse.dto.request.OccupyRoomRequest;
import com.acpulse.dto.response.RoomResponse;
import com.acpulse.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RoomController {

    @Autowired
    private RoomService roomService;

    //  Search for a specific room by room number
    @GetMapping("/rooms/search")
    public ResponseEntity<RoomResponse> searchRoom(@RequestParam String roomNumber) {
        RoomResponse response = roomService.searchRoom(roomNumber);
        return ResponseEntity.ok(response);
    }

    //  Get all rooms with optional search and status filters
    @GetMapping("/rooms")
    public ResponseEntity<Page<RoomResponse>> getAllRooms(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomResponse> response = roomService.getAllRooms(search, status, pageable);
        return ResponseEntity.ok(response);
    }

    //  Lecturer occupies a room (pass lecturerId manually)
    @PostMapping("/lecturer/occupy-room")
    public ResponseEntity<Map<String, Object>> occupyRoom(
            @RequestParam Integer lecturerId,
            @Valid @RequestBody OccupyRoomRequest request) {
        Map<String, Object> response = roomService.occupyRoom(lecturerId, request);
        return ResponseEntity.ok(response);
    }

    //  Lecturer extends room usage (pass lecturerId manually)
    @PutMapping("/lecturer/extend-room/{roomId}")
    public ResponseEntity<Map<String, String>> extendRoom(
            @RequestParam Integer lecturerId,
            @PathVariable Integer roomId,
            @Valid @RequestBody ExtendRoomRequest request) {
        Map<String, String> response = roomService.extendRoom(lecturerId, roomId, request);
        return ResponseEntity.ok(response);
    }

    //  Lecturer releases a room (pass lecturerId manually)
    @PostMapping("/lecturer/release-room/{roomId}")
    public ResponseEntity<Map<String, String>> releaseRoom(
            @RequestParam Integer lecturerId,
            @PathVariable Integer roomId) {
        Map<String, String> response = roomService.releaseRoom(lecturerId, roomId);
        return ResponseEntity.ok(response);
    }
}
