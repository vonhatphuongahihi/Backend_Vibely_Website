package com.example.vibely_backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.service.DocumentService;
import com.example.vibely_backend.service.JWTService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final JWTService jwtService;

    private final DocumentService documentService;

    @GetMapping("/levels")
    public ResponseEntity<ApiResponse> getLevels() {
        return ResponseEntity.ok(documentService.getAllLevels());
    }

    @GetMapping("/subjects/{levelId}")
    public ResponseEntity<ApiResponse> getSubjectsByLevel(@PathVariable String levelId) {
        return ResponseEntity.ok(documentService.getSubjectsByLevel(levelId));
    }

    // Lấy danh sách tài liệu theo bộ lọc
    @GetMapping
    public ResponseEntity<ApiResponse> getFilteredDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String subject) {
        return ResponseEntity.ok(documentService.getFilteredDocuments(query, level, subject));
    }

    // Lấy tài liệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDocumentById(@PathVariable String id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    // Lưu tài liệu
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveDocument(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserIdFromToken(token);
        String documentId = body.get("documentId");

        ApiResponse response = documentService.saveDocument(userId, documentId);

        // Xác định HTTP status dựa theo response status string
        HttpStatus status = HttpStatus.OK;
        if ("error".equalsIgnoreCase(response.getStatus())) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if ("fail".equalsIgnoreCase(response.getStatus())) {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(response);
    }

}
