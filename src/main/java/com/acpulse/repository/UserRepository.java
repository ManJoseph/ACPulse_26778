package com.acpulse.repository;

import com.acpulse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdentificationNumber(String identificationNumber);
    boolean existsByEmail(String email);
    boolean existsByIdentificationNumber(String identificationNumber);
    List<User> findByRole_RoleName(String roleName);
    List<User> findByNameContainingIgnoreCaseAndRole_RoleName(String name, String roleName);
}
