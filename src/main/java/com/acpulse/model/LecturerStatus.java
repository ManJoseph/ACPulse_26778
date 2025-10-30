package com.acpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturer_status")
public class LecturerStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "lecturer_id", nullable = false)
    private Integer lecturerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


    @Column(name = "current_room_id")
    private Integer currentRoomId;

    @Column(name = "custom_message")
    private String customMessage;

    @Column(name = "status_start_time", nullable = false)
    private LocalDateTime statusStartTime = LocalDateTime.now();

    @Column(name = "expected_end_time")
    private LocalDateTime expectedEndTime;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        IN_OFFICE, TEACHING, AWAY, AVAILABLE
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getLecturerId() { return lecturerId; }
    public void setLecturerId(Integer lecturerId) { this.lecturerId = lecturerId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Integer getCurrentRoomId() {
        return currentRoomId;
    }
    public void setCurrentRoomId(Integer currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public LocalDateTime getStatusStartTime() { return statusStartTime; }
    public void setStatusStartTime(LocalDateTime statusStartTime) {
        this.statusStartTime = statusStartTime;
    }

    public LocalDateTime getExpectedEndTime() { return expectedEndTime; }
    public void setExpectedEndTime(LocalDateTime expectedEndTime) {
        this.expectedEndTime = expectedEndTime;
    }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
