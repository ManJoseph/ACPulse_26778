package com.acpulse.repository;

import com.acpulse.model.LecturerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LecturerStatusRepository extends JpaRepository<LecturerStatus, Integer> {
    Optional<LecturerStatus> findByLecturerIdAndIsActive(Integer lecturerId, Boolean isActive);
    List<LecturerStatus> findByLecturerId(Integer lecturerId);
}
