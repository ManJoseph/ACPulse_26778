package com.acpulse.repository;

import com.acpulse.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<User> findByNameContainingIgnoreCase(String search, Pageable pageable);
    Page<User> findByNameContainingIgnoreCaseAndRole_RoleNameIgnoreCase(String search, String role, Pageable pageable);
    Page<User> findByRole_RoleNameIgnoreCase(String roleName, Pageable pageable);
}
