package com.acpulse.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {
    private Integer id; // For updates
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    // lecturerId might be passed in PathVariable, but good to have here for consistency if needed
}
