package com.example.vibely_backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vibely_backend.dto.request.DocumentRequest;
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

    @PostMapping("/levels")
    public ResponseEntity<ApiResponse> createLevel(@RequestBody Map<String, String> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            documentService.createLevel(request.get("name"))
        );
    }

    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse> createSubject(@RequestBody Map<String, String> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            documentService.createSubject(request.get("name"), request.get("levelId"))
        );
    }

    @GetMapping("/levels")
    public ResponseEntity<ApiResponse> getLevels() {
        return ResponseEntity.ok(documentService.getAllLevels());
    }

    @GetMapping("/subjects/{levelId}")
    public ResponseEntity<ApiResponse> getSubjectsByLevel(@PathVariable String levelId) {
        return ResponseEntity.ok(documentService.getSubjectsByLevel(levelId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createDocument(@RequestBody DocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.createDocument(request));
    }

    // Lấy danh sách tài liệu theo bộ lọc
    @GetMapping
    public ResponseEntity<ApiResponse> getFilteredDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String subject
    ) {
        return ResponseEntity.ok(documentService.getFilteredDocuments(query, level, subject));
    }

    // Lấy tài liệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDocumentById(@PathVariable String id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    // Cập nhật tài liệu
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDocument(@PathVariable String id, @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(documentService.updateDocument(id, request));
    }

    // Xóa tài liệu
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDocument(@PathVariable String id) {
        return ResponseEntity.ok(documentService.deleteDocument(id));
    }

    // Lưu tài liệu
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveDocument(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> body) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserIdFromToken(token);
        String documentId = body.get("documentId");

        ApiResponse response = documentService.saveDocument(userId, documentId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
