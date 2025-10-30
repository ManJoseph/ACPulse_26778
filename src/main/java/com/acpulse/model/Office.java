package com.acpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "offices")
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "office_number", unique = true, nullable = false)
    private String officeNumber;

    @Column(name = "office_name")
    private String officeName;

    private String building;

    private String floor;

    // ManyToOne: Office → User (unidirectional, staff)
    @Column(name = "staff_user_id")
    private Integer staffUserId;

    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.CLOSED;

    @Column(name = "regular_open_time")
    private LocalTime regularOpenTime;

    @Column(name = "regular_close_time")
    private LocalTime regularCloseTime;

    @Column(name = "phone_extension")
    private String phoneExtension;

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum AvailabilityStatus {
        OPEN, CLOSED, BUSY
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }

    public String getOfficeName() { return officeName; }
    public void setOfficeName(String officeName) { this.officeName = officeName; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public Integer getStaffUserId() { return staffUserId; }
    public void setStaffUserId(Integer staffUserId) { this.staffUserId = staffUserId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public LocalTime getRegularOpenTime() { return regularOpenTime; }
    public void setRegularOpenTime(LocalTime regularOpenTime) {
        this.regularOpenTime = regularOpenTime;
    }

    public LocalTime getRegularCloseTime() { return regularCloseTime; }
    public void setRegularCloseTime(LocalTime regularCloseTime) {
        this.regularCloseTime = regularCloseTime;
    }

    public String getPhoneExtension() { return phoneExtension; }
    public void setPhoneExtension(String phoneExtension) {
        this.phoneExtension = phoneExtension;
    }

    public LocalDateTime getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}