package com.acpulse.repository;

import com.acpulse.model.LecturerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LecturerStatusRepository extends JpaRepository<LecturerStatus, Integer> {
    Optional<LecturerStatus> findByLecturer_IdAndIsActive(Integer lecturerId, Boolean isActive);
    List<LecturerStatus> findByLecturer_Id(Integer lecturerId);
}
