package com.example.vibely_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vibely_backend.dto.request.QuizRequest;
import com.example.vibely_backend.dto.request.QuizUpdateWrapper;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.entity.Quiz;
import com.example.vibely_backend.repository.QuizRepository;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public ApiResponse createQuiz(Quiz quiz) {
        if (quiz.getQuizTitle() == null || quiz.getQuizTitle().isEmpty() ||
            quiz.getIcon() == null || quiz.getIcon().isEmpty() ||
            quiz.getQuizQuestions() == null || quiz.getQuizQuestions().isEmpty()) {
            return new ApiResponse(400, "Vui lòng cung cấp đầy đủ thông tin.", null);
        }

        quizRepository.save(quiz);
        return new ApiResponse(201, "Quiz đã được tạo thành công!", quiz);
    }

    public ApiResponse getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return new ApiResponse(200, "Lấy danh sách quiz thành công!", quizzes);
    }

    public ApiResponse getQuizById(String id) {
        return quizRepository.findById(id)
            .map(quiz -> new ApiResponse(200, "Lấy quiz thành công!", quiz))
            .orElse(new ApiResponse(404, "Quiz không tồn tại.", null));
    }

    public ApiResponse updateQuiz(String id, QuizUpdateWrapper wrapper) {
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));

        // Nếu có updateQuiz thì cập nhật toàn bộ quiz
        if (wrapper.getUpdateQuiz() != null) {
            QuizRequest updateQuiz = wrapper.getUpdateQuiz();
            quiz.setIcon(updateQuiz.getIcon());
            quiz.setQuizTitle(updateQuiz.getQuizTitle());
            quiz.setQuizQuestions(updateQuiz.getQuizQuestions());
        }

        // Nếu chỉ muốn update quizQuestions
        if (wrapper.getUpdateQuizQuestions() != null && !wrapper.getUpdateQuizQuestions().isEmpty()) {
            quiz.setQuizQuestions(wrapper.getUpdateQuizQuestions());
        }

        quizRepository.save(quiz);
        return new ApiResponse(200, "Cập nhật quiz thành công!", quiz);
    }

    public ApiResponse deleteQuiz(String id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        if (quiz.isEmpty()) {
            return new ApiResponse(404, "Quiz không tồn tại.", null);
        }

        quizRepository.deleteById(id);
        return new ApiResponse(200, "Xóa quiz thành công!", quiz.get());
    }
}

