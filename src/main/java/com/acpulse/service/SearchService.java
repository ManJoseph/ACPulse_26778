package com.acpulse.service;

import com.acpulse.dto.response.GlobalSearchResponse;
import com.acpulse.model.Room;
import com.acpulse.model.User;
import com.acpulse.repository.RoomRepository;
import com.acpulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    public List<GlobalSearchResponse> globalSearch(String query) {
        List<GlobalSearchResponse> results = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        // Search rooms
        List<Room> rooms = roomRepository.findByRoomNameContainingIgnoreCase(query);
        results.addAll(rooms.stream()
                .map(room -> new GlobalSearchResponse(
                        room.getId().toString(),
                        "Room",
                        room.getRoomName(),
                        "Type: " + room.getRoomType() + ", Capacity: " + room.getCapacity(),
                        "/rooms/" + room.getId())) // Path to room details
                .collect(Collectors.toList()));

        // Search lecturers by name (assuming role is 'LECTURER')
        List<User> lecturers = userRepository.findByNameContainingIgnoreCaseAndRole_RoleName(query, "LECTURER");
        results.addAll(lecturers.stream()
                .map(lecturer -> new GlobalSearchResponse(
                        lecturer.getId().toString(),
                        "Lecturer",
                        lecturer.getName(),
                        "Department: " + (lecturer.getDepartment() != null ? lecturer.getDepartment() : "N/A"),
                        "/lecturers/" + lecturer.getId())) // Path to lecturer profile
                .collect(Collectors.toList()));

        return results;
    }
}
