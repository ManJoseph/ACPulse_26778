package com.acpulse.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerResponse {
    private Integer id;
    private String name;
    private String email;
    private String department;
    private String phoneNumber;
    private String status; // Current status
    private String officeName; // Current office if assigned
}
