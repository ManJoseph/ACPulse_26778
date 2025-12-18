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
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Map<String, String> NAVIGATION_ITEMS = Map.of(
            "Change Password", "/profile#password",
            "Notifications", "/notifications",
            "Settings", "/profile",
            "Logout", "/logout", // Handled by frontend usually, but good for search
            "Rooms", "/rooms",
            "Lecturers", "/lecturers"
    );

    public List<GlobalSearchResponse> globalSearch(String query) {
        List<GlobalSearchResponse> results = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase();

        // 1. Navigation items (High Priority)
        NAVIGATION_ITEMS.forEach((name, path) -> {
            if (name.toLowerCase().contains(lowerQuery)) {
                results.add(new GlobalSearchResponse(
                        "NAV-" + name.hashCode(),
                        "Navigation",
                        name,
                        "Go to " + name,
                        path
                ));
            }
        });

        // 2. Search rooms
        List<Room> rooms = roomRepository.findByRoomNameContainingIgnoreCase(query);
        results.addAll(rooms.stream()
                .map(room -> new GlobalSearchResponse(
                        room.getId().toString(),
                        "Room",
                        room.getRoomName(),
                        "Type: " + room.getRoomType() + ", Capacity: " + room.getCapacity(),
                        room.getId())) // Payload is ID
                .collect(Collectors.toList()));

        // 3. Search lecturers by name (assuming role is 'LECTURER')
        List<User> lecturers = userRepository.findByNameContainingIgnoreCaseAndRole_RoleName(query, "LECTURER");
        results.addAll(lecturers.stream()
                .map(lecturer -> new GlobalSearchResponse(
                        lecturer.getId().toString(),
                        "Lecturer",
                        lecturer.getName(),
                        "Department: " + (lecturer.getDepartment() != null ? lecturer.getDepartment() : "N/A"),
                        lecturer.getId())) // Payload is ID
                .collect(Collectors.toList()));

        return results;
    }
}
