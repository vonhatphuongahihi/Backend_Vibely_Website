package com.example.vibely_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vibely_backend.dto.request.UpdateQuizQuestionsWrapper;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.service.QuizService;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getQuizById(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateQuizQuestions(
            @PathVariable String id,
            @RequestBody UpdateQuizQuestionsWrapper request
    ) {
        return ResponseEntity.ok(quizService.updateQuizQuestionsByUser(id, request.getUpdateQuizQuestions()));
    }
}
