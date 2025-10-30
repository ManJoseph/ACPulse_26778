package com.acpulse.dto.request;

import jakarta.validation.constraints.*;

public class UpdateStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;

    private String customMessage;

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
}
