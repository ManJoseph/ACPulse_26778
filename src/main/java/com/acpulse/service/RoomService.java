package com.acpulse.service;

import com.acpulse.dto.request.ExtendRoomRequest;
import com.acpulse.dto.request.OccupyRoomRequest;
import com.acpulse.dto.response.RoomResponse;
import com.acpulse.exception.BadRequestException;
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

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LecturerStatusRepository lecturerStatusRepository;

    public RoomResponse searchRoom(String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomNumber));

        return buildRoomResponse(room);
    }

    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + id));
        return buildRoomResponse(room);
    }

    public Page<RoomResponse> getAllRooms(String search, String status, Pageable pageable) {
        Page<Room> roomPage;
        if (search != null && !search.trim().isEmpty()) {
            if (status != null && !status.trim().isEmpty()) {
                roomPage = roomRepository.findByStatusAndRoomNameContainingIgnoreCase(Room.RoomStatus.valueOf(status.toUpperCase()), search, pageable);
            } else {
                roomPage = roomRepository.findByRoomNameContainingIgnoreCase(search, pageable);
            }
        } else {
            if (status != null && !status.trim().isEmpty()) {
                roomPage = roomRepository.findByStatus(Room.RoomStatus.valueOf(status.toUpperCase()), pageable);
            } else {
                roomPage = roomRepository.findAll(pageable);
            }
        }
        return roomPage.map(this::buildRoomResponse);
    }

    @Transactional
    public Map<String, Object> occupyRoom(Integer lecturerId, OccupyRoomRequest request) {
        // Find room
        Room room = roomRepository.findByRoomNumber(request.getRoomNumber())
                .orElseThrow(() -> new NotFoundException("Room not found: " + request.getRoomNumber()));

        // Check if room is available
        if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
            throw new BadRequestException("Room is already occupied");
        }

        // Check if end time is in the future
        if (request.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("End time must be in the future");
        }

        // Get lecturer
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new NotFoundException("Lecturer not found"));

        // Update room
        room.setStatus(Room.RoomStatus.OCCUPIED);
        room.setCurrentLecturer(lecturer);
        room.setOccupiedUntil(request.getEndTime());
        room.setStatusUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);

        // Deactivate previous lecturer status
        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    status.setIsActive(false);
                    lecturerStatusRepository.save(status);
                });

        // Create new lecturer status
        LecturerStatus status = new LecturerStatus();
        status.setLecturer(lecturer);
        status.setStatus(LecturerStatus.Status.TEACHING);
        status.setCurrentRoom(room);
        status.setCustomMessage(request.getCustomMessage());
        status.setExpectedEndTime(request.getEndTime());
        status = lecturerStatusRepository.save(status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Room occupied successfully");
        response.put("roomId", room.getId());
        response.put("statusId", status.getId());
        response.put("occupiedUntil", request.getEndTime());
        return response;
    }

    @Transactional

    public Map<String, String> extendRoom(Integer lecturerId, Integer roomId, ExtendRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found"));

        // Debug logging
        System.out.println("=== EXTEND ROOM DEBUG ===");
        System.out.println("Lecturer ID trying to extend: " + lecturerId);
        System.out.println("Room ID: " + roomId);
        System.out.println("Room current lecturer: " + (room.getCurrentLecturer() != null ? room.getCurrentLecturer().getId() : "null"));
        System.out.println("New end time: " + request.getNewEndTime());

        // CRITICAL FIX: Check both room.currentLecturer AND lecturer status
        boolean isOccupiedByLecturer = false;
        
        // Check 1: Room's currentLecturer field
        if (room.getCurrentLecturer() != null && lecturerId.equals(room.getCurrentLecturer().getId())) {
            isOccupiedByLecturer = true;
        }
        
        // Check 2: Lecturer's active status (fallback)
        if (!isOccupiedByLecturer) {
            Optional<LecturerStatus> statusOpt = lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true);
            if (statusOpt.isPresent() && statusOpt.get().getCurrentRoom() != null) {
                if (statusOpt.get().getCurrentRoom().getId().equals(roomId)) {
                    isOccupiedByLecturer = true;
                    // Sync the room's currentLecturer field
                    User lecturer = userRepository.findById(lecturerId)
                            .orElseThrow(() -> new NotFoundException("Lecturer not found"));
                    room.setCurrentLecturer(lecturer);
                    System.out.println("FIXED: Synced room.currentLecturer from lecturer status");
                }
            }
        }
        
        if (!isOccupiedByLecturer) {
            // Check for "Zombie" state: Room says OCCUPIED but currentLecturer is null
            // OR Room says AVAILABLE (expired?) but user thinks they are here.
            
            // If room is AVAILABLE, checking status to see if we SHOULD have been there
            if (room.getStatus() == Room.RoomStatus.AVAILABLE) {
                 System.out.println("Room is AVAILABLE. Proceeding with extension (re-occupation).");
                 isOccupiedByLecturer = true;
            } else if (room.getStatus() == Room.RoomStatus.OCCUPIED && room.getCurrentLecturer() == null) {
                  System.out.println("Zombie state detected (Occupied with null lecturer). Allowing extension.");
                  isOccupiedByLecturer = true;
            }
        }
        
        if (!isOccupiedByLecturer) {
             throw new BadRequestException("You are not currently occupying this room");
        }

        // Check if new end time is in the future
        if (request.getNewEndTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("New end time must be in the future");
        }

        // Update room
        room.setOccupiedUntil(request.getNewEndTime());
        room.setStatusUpdatedAt(LocalDateTime.now());
        
        // CRITICAL: Ensure we fix the room state if it was broken/zombie
        if (room.getStatus() != Room.RoomStatus.OCCUPIED || room.getCurrentLecturer() == null) {
            room.setStatus(Room.RoomStatus.OCCUPIED);
            // We need to fetch the lecturer entity if we don't have it (we only have ID)
            if (room.getCurrentLecturer() == null) {
                User lecturer = userRepository.findById(lecturerId)
                        .orElseThrow(() -> new NotFoundException("Lecturer not found"));
                room.setCurrentLecturer(lecturer);
            }
        }
        
        roomRepository.save(room);

        // Update lecturer status
        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    status.setExpectedEndTime(request.getNewEndTime());
                    lecturerStatusRepository.save(status);
                });

        Map<String, String> response = new HashMap<>();
        response.put("message", "Room occupation extended until " + request.getNewEndTime());
        return response;
    }

    @Transactional
    public Map<String, String> releaseRoom(Integer lecturerId, Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found"));

        // Debug logging
        System.out.println("=== RELEASE ROOM DEBUG ===");
        System.out.println("Lecturer ID trying to release: " + lecturerId);
        System.out.println("Room ID: " + roomId);
        System.out.println("Room current lecturer: " + (room.getCurrentLecturer() != null ? room.getCurrentLecturer().getId() : "null"));
        System.out.println("Room status: " + room.getStatus());

        // CRITICAL FIX: Check both room.currentLecturer AND lecturer status
        // This handles cases where room was occupied via status update (not occupyRoom)
        boolean isOccupiedByLecturer = false;
        
        // Check 1: Room's currentLecturer field
        if (room.getCurrentLecturer() != null && lecturerId.equals(room.getCurrentLecturer().getId())) {
            isOccupiedByLecturer = true;
        }
        
        // Check 2: Lecturer's active status (fallback for status-based occupations)
        // AND Check 3: Idempotency - If room is AVAILABLE, allow "release" to ensure status cleanup
        if (!isOccupiedByLecturer) {
            
            // If room is occupied by SOMEONE ELSE, reject
            if (room.getCurrentLecturer() != null && !lecturerId.equals(room.getCurrentLecturer().getId())) {
                throw new BadRequestException("Room is currently occupied by another lecturer (" + room.getCurrentLecturer().getName() + ")");
            }

            // If room is AVAILABLE, checking status to see if we SHOULD have been there
            if (room.getStatus() == Room.RoomStatus.AVAILABLE) {
                 System.out.println("Room is AVAILABLE. Proceeding with idempotent release to cleanup status.");
                 isOccupiedByLecturer = true; // Allow proceeding
            } else {
                 // Room is occupied (but not by us?) or some other state.
                 // Fallback: Check if status says we are here
                Optional<LecturerStatus> statusOpt = lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true);
                if (statusOpt.isPresent() && statusOpt.get().getCurrentRoom() != null) {
                    if (statusOpt.get().getCurrentRoom().getId().equals(roomId)) {
                        isOccupiedByLecturer = true;
                        // Sync the room's currentLecturer field
                        User lecturer = userRepository.findById(lecturerId)
                                .orElseThrow(() -> new NotFoundException("Lecturer not found"));
                        room.setCurrentLecturer(lecturer);
                        System.out.println("FIXED: Synced room.currentLecturer from lecturer status");
                    }
                }
            }
        }
        
        if (!isOccupiedByLecturer) {
             // One last check: if room is AVAILABLE, we already set isOccupiedByLecturer=true.
             // So here means: Room occupied by someone else OR Room occupied by null but status checks failed?
             // Actually, if Room is AVAILABLE, we allow it.
             
             // So this only throws if Room is OCCUPIED by someone else (caught above) 
             // OR Room is OCCUPIED by null (which is effectively AVAILABLE? No, status could be OCCUPIED but currentLecturer null)
             
             // If Status is OCCUPIED but currentLecturer is null:
             if (room.getStatus() == Room.RoomStatus.OCCUPIED && room.getCurrentLecturer() == null) {
                  // This is a zombie state. We allow ensuring this user can clear it if they think they are here.
                  // But safely, we should probably allow it.
                  System.out.println("Zombie state detected (Occupied with null lecturer). Allowing release.");
                  isOccupiedByLecturer = true;
             }
        }
        
        if (!isOccupiedByLecturer) {
             throw new BadRequestException("You are not currently occupying this room");
        }

        // Release room
        room.setStatus(Room.RoomStatus.AVAILABLE);
        room.setCurrentLecturer(null);
        room.setOccupiedUntil(null);
        room.setLastExpiryEmailSentAt(null); // CRITICAL: Reset email tracking
        room.setStatusUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);

        // Update lecturer status
        lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturerId, true)
                .ifPresent(status -> {
                    status.setStatus(LecturerStatus.Status.AVAILABLE);
                    status.setCurrentRoom(null);
                    status.setExpectedEndTime(null);
                    lecturerStatusRepository.save(status);
                });

        Map<String, String> response = new HashMap<>();
        response.put("message", "Room released successfully");
        return response;
    }

    private RoomResponse buildRoomResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomName(room.getRoomName());
        response.setBuilding(room.getBuilding());
        response.setFloor(room.getFloor());
        response.setCapacity(room.getCapacity());
        response.setRoomType(room.getRoomType().name());
        response.setStatus(room.getStatus().name());

        if (room.getOfficeOwner() != null) {
            response.setOfficeOwnerId(room.getOfficeOwner().getId());
            response.setOfficeOwnerName(room.getOfficeOwner().getName());
        }

        // Add lecturer info if occupied
        if (room.getStatus() == Room.RoomStatus.OCCUPIED && room.getCurrentLecturer() != null) {
            User lecturer = room.getCurrentLecturer();
            response.setCurrentLecturerName(lecturer.getName());

            response.setOccupiedUntil(room.getOccupiedUntil());
            response.setOccupiedSince(room.getStatusUpdatedAt());

            // Get occupation message
            lecturerStatusRepository.findByLecturer_IdAndIsActive(lecturer.getId(), true)
                    .ifPresent(status -> response.setOccupationMessage(status.getCustomMessage()));
        }

        return response;
    }

    // --- Admin Methods ---

    @Transactional
    public RoomResponse createRoom(com.acpulse.dto.request.RoomRequest request) {
        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new BadRequestException("Room with number " + request.getRoomNumber() + " already exists.");
        }

        Room room = new Room();
        updateRoomFromRequest(room, request);
        room.setStatus(Room.RoomStatus.AVAILABLE); // Default status
        
        Room savedRoom = roomRepository.save(room);
        return buildRoomResponse(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(Integer id, com.acpulse.dto.request.RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + id));

        // Check uniqueness of room number if changed
        if (!room.getRoomNumber().equals(request.getRoomNumber()) && 
            roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
             throw new BadRequestException("Room with number " + request.getRoomNumber() + " already exists.");
        }

        updateRoomFromRequest(room, request);
        Room savedRoom = roomRepository.save(room);
        return buildRoomResponse(savedRoom);
    }

    @Transactional
    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new NotFoundException("Room not found with id: " + id);
        }
        // Potential check: Is room occupied? If so, maybe prevent delete or force release.
        // For now, let's stick to basics. JPA might complain about FK constraints if occupied/referenced.
        // Assuming cascade or simple delete for now.
        roomRepository.deleteById(id);
    }

    private void updateRoomFromRequest(Room room, com.acpulse.dto.request.RoomRequest request) {
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomName(request.getRoomName());
        room.setBuilding(request.getBuilding());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        try {
            room.setRoomType(Room.RoomType.valueOf(request.getRoomType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid room type: " + request.getRoomType());
        }

        // Handle Office Owner assignment
        if (request.getOfficeOwnerId() != null) {
            User owner = userRepository.findById(request.getOfficeOwnerId())
                    .orElseThrow(() -> new NotFoundException("Office owner not found with id: " + request.getOfficeOwnerId()));
            room.setOfficeOwner(owner);
        } else {
             // If null is explicitly sent (or conceptually needed), we unassign.
             // Usually request param absence means "don't change" or "null". 
             // Here simpler to assume if it's null in request, clear it? Or only if intended.
             // Given it's a DTO, null might mean "no change" or "remove".
             // For simplicity: if request has logic to clear, we'd need a flag or explicit null handling.
             // Let's assume if it is null, we set it to null (clear assignment), AS LONG AS IT IS AN OFFICE.
             // Actually, safer: set to null unless we want partial updates. But this method overwrites fields.
             room.setOfficeOwner(null);
        }
    }
}
