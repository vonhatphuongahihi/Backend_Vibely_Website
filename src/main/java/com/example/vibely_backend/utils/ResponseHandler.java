package com.example.vibely_backend.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResponseHandler {
    
    public ResponseEntity<?> success(String message, Object data, int statusCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.status(statusCode).body(response);
    }

    public ResponseEntity<?> error(String message, int statusCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(statusCode).body(response);
    }

    public ResponseEntity<?> error(String message, int statusCode, String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("error", error);
        return ResponseEntity.status(statusCode).body(response);
    }
} 