package com.example.vibely_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.vibely_backend.dto.request.DocumentRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.dto.response.DocumentResponse;
import com.example.vibely_backend.entity.DocumentUser;
import com.example.vibely_backend.entity.Level;
import com.example.vibely_backend.entity.Subject;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.DocumentRepository;
import com.example.vibely_backend.repository.LevelRepository;
import com.example.vibely_backend.repository.SubjectRepository;
import com.example.vibely_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final LevelRepository levelRepository;
    private final SubjectRepository subjectRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public ApiResponse createLevel(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ApiResponse("error", "Tên cấp học không được để trống", null);
        }
        if (levelRepository.findByName(name).isPresent()) {
            return new ApiResponse("error", "Cấp học đã tồn tại", null);
        }

        Level level = new Level();
        level.setName(name);
        levelRepository.save(level);
        return new ApiResponse("success", "Tạo cấp học thành công", level);
    }

    public ApiResponse createSubject(String name, String levelId) {
        if (name == null || levelId == null) {
            return new ApiResponse("error", "Tên môn học và cấp học là bắt buộc", null);
        }
        Optional<Level> levelOpt = levelRepository.findById(levelId);
        if (levelOpt.isEmpty()) {
            return new ApiResponse("error", "Không tìm thấy cấp học", null);
        }

        Level level = levelOpt.get();

        if (subjectRepository.findByNameAndLevelId(name, levelId).isPresent()) {
            return new ApiResponse("error", "Môn học đã tồn tại cho cấp học này", null);
        }

        Subject subject = new Subject();
        subject.setName(name);
        subject.setLevelId(levelId);
        subjectRepository.save(subject);
        return new ApiResponse("success", "Tạo môn học thành công", subject);
    }

    public ApiResponse getAllLevels() {
        List<Level> levels = levelRepository.findAll();
        return new ApiResponse("success", "Lấy danh sách cấp học thành công", levels);
    }

    public ApiResponse getSubjectsByLevel(String levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("Cấp học không hợp lệ"));

        List<Subject> subjects = subjectRepository.findByLevelId(levelId);

        // Trả về danh sách chỉ gồm id và name (dưới dạng Map)
        List<Map<String, Object>> result = subjects.stream().map(subject -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", subject.getId());
            map.put("name", subject.getName());
            return map;
        }).collect(Collectors.toList());

        return new ApiResponse("success", "Lấy danh sách môn học thành công", result);
    }

    public ApiResponse createDocument(DocumentRequest request) {
        Optional<Level> levelOpt = levelRepository.findById(request.getLevelId());
        if (levelOpt.isEmpty())
            return new ApiResponse("error", "Cấp học không hợp lệ", null);

        Optional<Subject> subjectOpt = subjectRepository.findById(request.getSubjectId());
        if (subjectOpt.isEmpty())
            return new ApiResponse("error", "Môn học không hợp lệ", null);

        DocumentUser doc = new DocumentUser();
        doc.setTitle(request.getTitle());
        doc.setPages(request.getPages());
        doc.setFileType(request.getFileType());
        doc.setFileUrl(request.getFileUrl());
        doc.setLevelId(levelOpt.get().getId());
        doc.setSubjectId(subjectOpt.get().getId());
        doc.setUploadDate(LocalDateTime.now());

        documentRepository.save(doc);

        Level level = levelOpt.get();
        Subject subject = subjectOpt.get();
        DocumentResponse response = new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getPages(),
                doc.getFileType(),
                doc.getFileUrl(),
                level.getId(),
                level.getName(),
                subject.getId(),
                subject.getName(),
                doc.getUploadDate(),
                doc.getUpdatedAt());

        return new ApiResponse("success", "Tạo tài liệu thành công", response);
    }

    public ApiResponse getFilteredDocuments(String query, String levelId, String subjectId) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (levelId != null && !levelId.isBlank()) {
            criteriaList.add(Criteria.where("level_id").is(levelId));
        }

        if (subjectId != null && !subjectId.isBlank()) {
            criteriaList.add(Criteria.where("subject_id").is(subjectId));
        }

        if (query != null && !query.isBlank()) {
            criteriaList.add(Criteria.where("title").regex(query, "i"));
        }

        Query queryObj;

        if (criteriaList.isEmpty()) {
            queryObj = new Query();
        } else {
            Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            queryObj = new Query(criteria);
        }

        queryObj.with(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<DocumentUser> documents = mongoTemplate.find(queryObj, DocumentUser.class);

        List<DocumentResponse> responses = documents.stream().map(doc -> {
            Level level = null;
            Subject subject = null;
            if (doc.getLevelId() != null) {
                level = levelRepository.findById(doc.getLevelId()).orElse(null);
            }
            if (doc.getSubjectId() != null) {
                subject = subjectRepository.findById(doc.getSubjectId()).orElse(null);
            }
            return new DocumentResponse(
                    doc.getId(),
                    doc.getTitle(),
                    doc.getPages(),
                    doc.getFileType(),
                    doc.getFileUrl(),
                    level != null ? level.getId() : null,
                    level != null ? level.getName() : null,
                    subject != null ? subject.getId() : null,
                    subject != null ? subject.getName() : null,
                    doc.getUploadDate(),
                    doc.getUpdatedAt());
        }).collect(Collectors.toList());

        return new ApiResponse("success", "Lấy danh sách tài liệu thành công", responses);
    }

    public ApiResponse getDocumentById(String id) {
        DocumentUser doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

        Level level = null;
        Subject subject = null;
        if (doc.getLevelId() != null) {
            level = levelRepository.findById(doc.getLevelId()).orElse(null);
        }
        if (doc.getSubjectId() != null) {
            subject = subjectRepository.findById(doc.getSubjectId()).orElse(null);
        }

        // Log để debug
        System.out.println("Document from DB: " + doc);

        DocumentResponse response = new DocumentResponse();
        response.setId(doc.getId());
        response.setTitle(doc.getTitle());
        response.setPages(doc.getPages());
        response.setFileType(doc.getFileType());
        response.setFileUrl(doc.getFileUrl());
        response.setLevelId(level != null ? level.getId() : null);
        response.setLevelName(level != null ? level.getName() : null);
        response.setSubjectId(subject != null ? subject.getId() : null);
        response.setSubjectName(subject != null ? subject.getName() : null);
        response.setUploadDate(doc.getUploadDate());
        response.setUpdatedAt(doc.getUpdatedAt());

        // Log để debug
        System.out.println("Response object: " + response);

        return new ApiResponse("success", "Lấy tài liệu theo ID thành công", response);
    }

    public ApiResponse updateDocument(String id, DocumentRequest req) {
        DocumentUser doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

        if (req.getLevelId() != null) {
            Level level = levelRepository.findById(req.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Cấp học không hợp lệ"));
            doc.setLevelId(level.getId());
        }

        if (req.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(req.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Môn học không hợp lệ"));
            doc.setSubjectId(subject.getId());
        }

        doc.setTitle(req.getTitle());
        doc.setPages(req.getPages());
        doc.setFileType(req.getFileType());
        doc.setFileUrl(req.getFileUrl());
        documentRepository.save(doc);

        Level level = null;
        Subject subject = null;
        if (doc.getLevelId() != null) {
            level = levelRepository.findById(doc.getLevelId()).orElse(null);
        }
        if (doc.getSubjectId() != null) {
            subject = subjectRepository.findById(doc.getSubjectId()).orElse(null);
        }
        DocumentResponse response = new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getPages(),
                doc.getFileType(),
                doc.getFileUrl(),
                level != null ? level.getId() : null,
                level != null ? level.getName() : null,
                subject != null ? subject.getId() : null,
                subject != null ? subject.getName() : null,
                doc.getUploadDate(),
                doc.getUpdatedAt());

        return new ApiResponse("success", "Cập nhật tài liệu thành công", response);
    }

    public ApiResponse deleteDocument(String id) {
        if (!documentRepository.existsById(id)) {
            return new ApiResponse("error", "Không tìm thấy tài liệu", null);
        }
        documentRepository.deleteById(id);
        return new ApiResponse("success", "Xóa tài liệu thành công", null);
    }

    public ApiResponse saveDocument(String userId, String documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        DocumentUser document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại"));

        if (user.getSavedDocuments().contains(documentId)) {
            return new ApiResponse("error", "Tài liệu đã được lưu trước đó", null);
        }

        user.getSavedDocuments().add(documentId);
        userRepository.save(user);

        return new ApiResponse("success", "Lưu tài liệu thành công", user.getSavedDocuments());
    }

}
