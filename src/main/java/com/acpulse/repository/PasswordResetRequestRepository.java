package com.acpulse.repository;

import com.acpulse.model.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Integer> {
    Optional<PasswordResetRequest> findByToken(String token);
    List<PasswordResetRequest> findByStatus(PasswordResetRequest.RequestStatus status);
}
