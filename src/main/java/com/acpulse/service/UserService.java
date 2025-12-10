package com.acpulse.service;

import com.acpulse.dto.request.UpdateProfileRequest;
import com.acpulse.exception.NotFoundException;
import com.acpulse.model.User;
import com.acpulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }

        return userRepository.save(user);
    }
}
