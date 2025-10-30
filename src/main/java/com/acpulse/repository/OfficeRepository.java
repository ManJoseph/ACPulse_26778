package com.acpulse.repository;

import com.acpulse.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OfficeRepository extends JpaRepository<Office, Integer> {
    Optional<Office> findByStaffUserId(Integer staffUserId);
    Optional<Office> findByOfficeNumber(String officeNumber);
}
