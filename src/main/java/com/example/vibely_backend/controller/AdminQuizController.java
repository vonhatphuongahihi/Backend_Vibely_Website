package com.example.vibely_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vibely_backend.dto.request.QuizRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.entity.Quiz;
import com.example.vibely_backend.service.QuizService;

@RestController
@RequestMapping("admin/quiz")
public class AdminQuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping
    public ResponseEntity<ApiResponse> createQuiz(@RequestBody Quiz quiz) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(quiz));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getQuizById(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateQuizByAdmin(
            @PathVariable String id,
            @RequestBody QuizRequest request
    ) {
        return ResponseEntity.ok(quizService.updateQuizByAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteQuiz(@PathVariable String id) {
        return ResponseEntity.ok(quizService.deleteQuiz(id));
    }
}
