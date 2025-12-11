package com.acpulse.repository;

import com.acpulse.model.VerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Integer> {
    List<VerificationRequest> findByStatus(VerificationRequest.Status status);
    Optional<VerificationRequest> findByUser_Id(Integer userId);
    long countByStatus(VerificationRequest.Status status);

    @Query("SELECT vr FROM VerificationRequest vr JOIN vr.user u " +
           "WHERE vr.status = :status AND (" +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(vr.submittedId) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<VerificationRequest> searchByStatusAndQuery(VerificationRequest.Status status, String query);
}
