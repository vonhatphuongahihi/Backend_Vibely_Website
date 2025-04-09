package com.example.vibely_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String categoryColor;
}