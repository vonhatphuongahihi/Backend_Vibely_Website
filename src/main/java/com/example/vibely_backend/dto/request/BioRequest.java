package com.example.vibely_backend.dto.request;

import lombok.Data;

@Data
public class BioRequest {
    private String bioText;
    private String liveIn;
    private String relationship;
    private String workplace;
    private String education;
    private String hometown;
}
