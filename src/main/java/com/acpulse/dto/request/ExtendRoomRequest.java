package com.acpulse.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ExtendRoomRequest {
    @NotNull(message = "New end time is required")
    private LocalDateTime newEndTime;

    // Getters and Setters
    public LocalDateTime getNewEndTime() { return newEndTime; }
    public void setNewEndTime(LocalDateTime newEndTime) { this.newEndTime = newEndTime; }
}
