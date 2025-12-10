package com.acpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // I will apply hashing algorithm after
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "identification_number", unique = true, nullable = false)
    private String identificationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    // Many-to-One: User → Role
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Many-to-One: User → Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String department;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-Many: User → LecturerStatus
    @OneToMany(mappedBy = "lecturer", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<LecturerStatus> lecturerStatuses = new ArrayList<>();

    // One-to-Many: User → VerificationRequest
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<VerificationRequest> verificationRequests = new ArrayList<>();

    // One-to-Many: User → Room (as currentLecturer)
    @OneToMany(mappedBy = "currentLecturer", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Room> occupiedRooms = new ArrayList<>();

    // One-to-Many: User → LectureSchedule
    @OneToMany(mappedBy = "lecturer", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<LectureSchedule> lectureSchedules = new ArrayList<>();

    // One-to-One: User → Office (for staff)
    @OneToOne(mappedBy = "staffUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private Office office;

    // enum for a user status after applying to use the system
    public enum UserStatus {
        PENDING, APPROVED, REJECTED
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getIdentificationNumber() { return identificationNumber; }
    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Relationship getters and setters
    public List<LecturerStatus> getLecturerStatuses() { return lecturerStatuses; }
    public void setLecturerStatuses(List<LecturerStatus> lecturerStatuses) { this.lecturerStatuses = lecturerStatuses; }

    public List<VerificationRequest> getVerificationRequests() { return verificationRequests; }
    public void setVerificationRequests(List<VerificationRequest> verificationRequests) { this.verificationRequests = verificationRequests; }

    public List<Room> getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(List<Room> occupiedRooms) { this.occupiedRooms = occupiedRooms; }

    public List<LectureSchedule> getLectureSchedules() { return lectureSchedules; }
    public void setLectureSchedules(List<LectureSchedule> lectureSchedules) { this.lectureSchedules = lectureSchedules; }

    public Office getOffice() { return office; }
    public void setOffice(Office office) { this.office = office; }
}
