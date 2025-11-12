package com.acpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// this entity is all about keeping the lecturer's status during a particular time
@Entity
@Table(name = "lecturer_status")
public class LecturerStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Many-to-One: LecturerStatus → User (lecturer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private User lecturer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Many-to-One: LecturerStatus → Room
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_room_id")
    private Room currentRoom;

    @Column(name = "custom_message")
    private String customMessage;

    @Column(name = "status_start_time", nullable = false)
    private LocalDateTime statusStartTime = LocalDateTime.now();

    @Column(name = "expected_end_time")
    private LocalDateTime expectedEndTime;

    // this confirms that the lecturer is actively working in that semester
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    //this shows different status of the lecturer in a particular moment
    public enum Status {
        IN_OFFICE, TEACHING, AWAY, AVAILABLE
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getLecturer() { return lecturer; }
    public void setLecturer(User lecturer) { this.lecturer = lecturer; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room currentRoom) { this.currentRoom = currentRoom; }

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
