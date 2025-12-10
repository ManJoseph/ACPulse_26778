package com.acpulse.controller;

import com.acpulse.dto.request.UpdateProfileRequest;
import com.acpulse.dto.response.UserProfileResponse;
import com.acpulse.exception.NotFoundException;
import com.acpulse.model.User;
import com.acpulse.repository.UserRepository;
import com.acpulse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        User updatedUser = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(new UserProfileResponse(updatedUser));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + userDetails.getUsername()));
        return ResponseEntity.ok(new UserProfileResponse(user));
    }
}
