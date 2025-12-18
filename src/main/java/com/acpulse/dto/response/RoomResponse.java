package com.acpulse.dto.response;

import java.time.LocalDateTime;

public class RoomResponse {
    private Integer id;
    private String roomNumber;
    private String roomName;
    private String building;
    private String floor;
    private Integer capacity;
    private String roomType;
    private String status;
    private String currentLecturerName;
    private LocalDateTime occupiedSince;
    private LocalDateTime occupiedUntil;
    private String occupationMessage;
    private Integer officeOwnerId;
    private String officeOwnerName;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentLecturerName() { return currentLecturerName; }
    public void setCurrentLecturerName(String currentLecturerName) {
        this.currentLecturerName = currentLecturerName;
    }

    public LocalDateTime getOccupiedSince() { return occupiedSince; }
    public void setOccupiedSince(LocalDateTime occupiedSince) {
        this.occupiedSince = occupiedSince;
    }

    public LocalDateTime getOccupiedUntil() { return occupiedUntil; }
    public void setOccupiedUntil(LocalDateTime occupiedUntil) {
        this.occupiedUntil = occupiedUntil;
    }

    public String getOccupationMessage() { return occupationMessage; }
    public void setOccupationMessage(String occupationMessage) {
        this.occupationMessage = occupationMessage;
    }

    public Integer getOfficeOwnerId() { return officeOwnerId; }
    public void setOfficeOwnerId(Integer officeOwnerId) { this.officeOwnerId = officeOwnerId; }

    public String getOfficeOwnerName() { return officeOwnerName; }
    public void setOfficeOwnerName(String officeOwnerName) { this.officeOwnerName = officeOwnerName; }
}
