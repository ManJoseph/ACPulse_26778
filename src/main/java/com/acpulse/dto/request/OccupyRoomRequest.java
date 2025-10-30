package com.acpulse.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class OccupyRoomRequest {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    private String customMessage;

    // Getters and Setters
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
}
