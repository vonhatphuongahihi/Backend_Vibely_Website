package com.example.vibely_backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    
    public static ResponseEntity<Map<String, Object>> response(HttpStatus status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return new ResponseEntity<>(response, status);
    }
    
    public static ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) {
        return response(status, message, null);
    }
} 