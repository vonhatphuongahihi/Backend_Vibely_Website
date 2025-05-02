package com.example.vibely_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {
    private String subject;
    private Date startTime;
    private Date endTime;
    private String categoryColor;
}