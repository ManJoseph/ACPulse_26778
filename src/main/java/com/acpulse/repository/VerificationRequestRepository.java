package com.acpulse.repository;

import com.acpulse.model.VerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Integer> {
    List<VerificationRequest> findByStatus(VerificationRequest.Status status);
    Optional<VerificationRequest> findByUser_Id(Integer userId);
}
