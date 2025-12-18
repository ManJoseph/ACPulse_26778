package com.acpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_number", unique = true, nullable = false)
    private String roomNumber;

    @Column(name = "room_name")
    private String roomName;

    private Integer capacity;

    private String building;

    private String floor;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    // Many-to-One: Room → User (current lecturer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_lecturer_id")
    private User currentLecturer;

    @Column(name = "occupied_until")
    private LocalDateTime occupiedUntil;

    // One-to-Many: Room → LecturerStatus
    @OneToMany(mappedBy = "currentRoom", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LecturerStatus> lecturerStatuses = new ArrayList<>();

    // One-to-Many: Room → LectureSchedule
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<LectureSchedule> lectureSchedules = new ArrayList<>();

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Many-to-One: Room → User (office owner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_owner_id")
    private User officeOwner;

    public enum RoomType {
        LECTURE_HALL, LAB, OFFICE, MEETING_ROOM
    }

    public enum RoomStatus {
        AVAILABLE, OCCUPIED, MAINTENANCE
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public User getCurrentLecturer() { return currentLecturer; }
    public void setCurrentLecturer(User currentLecturer) { this.currentLecturer = currentLecturer; }

    public LocalDateTime getOccupiedUntil() { return occupiedUntil; }
    public void setOccupiedUntil(LocalDateTime occupiedUntil) {
        this.occupiedUntil = occupiedUntil;
    }

    public List<LecturerStatus> getLecturerStatuses() { return lecturerStatuses; }
    public void setLecturerStatuses(List<LecturerStatus> lecturerStatuses) { this.lecturerStatuses = lecturerStatuses; }

    public List<LectureSchedule> getLectureSchedules() { return lectureSchedules; }
    public void setLectureSchedules(List<LectureSchedule> lectureSchedules) { this.lectureSchedules = lectureSchedules; }

    public LocalDateTime getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getOfficeOwner() { return officeOwner; }
    public void setOfficeOwner(User officeOwner) { this.officeOwner = officeOwner; }
}
