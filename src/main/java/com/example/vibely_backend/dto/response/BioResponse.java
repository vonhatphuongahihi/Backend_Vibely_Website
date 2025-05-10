package com.example.vibely_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BioResponse {
    private String bioText;
    private String liveIn;
    private String relationship;
    private String workplace;
    private String education;
    private String phone;
    private String hometown;
}
