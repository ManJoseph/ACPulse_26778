package com.acpulse.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Room name is required")
    private String roomName;

    @NotBlank(message = "Building is required")
    private String building;

    @NotNull(message = "Floor is required") // Floor can be '0' so NotBlank (for strings) isn't right if it were int, but Room model has String floor? Let's check. Room model has String floor.
    private String floor;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotBlank(message = "Room type is required")
    private String roomType; // We will convert to Enum in Service

    private Integer officeOwnerId; // Optional, for Office type
}
