package com.acpulse.controller;

import com.acpulse.model.Location;
import com.acpulse.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getLocations(
            @RequestParam(required = false) String type) {
        List<Location> locations;
        if (type != null) {
            Location.LocationType locationType = Location.LocationType.valueOf(type.toUpperCase());
            locations = locationRepository.findByType(locationType);
        } else {
            locations = locationRepository.findAll();
        }

        List<Map<String, Object>> simplifiedLocations = locations.stream()
                .map(loc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", loc.getId());
                    map.put("name", loc.getName());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(simplifiedLocations);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<Map<String, Object>>> getChildren(@PathVariable Integer id) {
        List<Location> children = locationRepository.findByParent_Id(id);
        
        List<Map<String, Object>> simplifiedChildren = children.stream()
                .map(loc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", loc.getId());
                    map.put("name", loc.getName());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(simplifiedChildren);
    }

    @GetMapping("/hierarchy/{id}")
    public ResponseEntity<Map<String, Object>> getHierarchy(@PathVariable Integer id) {
        Optional<Location> location = locationRepository.findById(id);

        if (location.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> path = new ArrayList<>();
        Location current = location.get();

        // Build path from current to root
        while (current != null) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", current.getId());
            node.put("name", current.getName());
            node.put("type", current.getType().name());
            path.add(0, node);

            Location parent = current.getParent();
            if (parent != null) {
                // Reload from repository to ensure parent is fully loaded
                current = locationRepository.findById(parent.getId()).orElse(null);
            } else {
                current = null;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("current", Map.of(
                "id", location.get().getId(),
                "name", location.get().getName(),
                "type", location.get().getType().name()
        ));
        response.put("path", path);

        return ResponseEntity.ok(response);
    }
}
