package com.example.vibely_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vibely_backend.dto.request.QuizRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.entity.Question;
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
            return new ApiResponse("error", "Vui lòng cung cấp đầy đủ thông tin.", null);
        }

        quizRepository.save(quiz);
        return new ApiResponse("success", "Quiz đã được tạo thành công!", quiz);
    }

    public ApiResponse getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return new ApiResponse("success", "Lấy danh sách quiz thành công!", quizzes);
    }

    public ApiResponse getQuizById(String id) {
        return quizRepository.findById(id)
                .map(quiz -> new ApiResponse("success", "Lấy quiz thành công!", quiz))
                .orElse(new ApiResponse("error", "Quiz không tồn tại.", null));
    }
    
    // Hàm cho admin: cập nhật toàn bộ thông tin quiz
    public ApiResponse updateQuizByAdmin(String id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));

        quiz.setQuizTitle(request.getQuizTitle());
        quiz.setIcon(request.getIcon());
        quiz.setQuizQuestions(request.getQuizQuestions());

        quizRepository.save(quiz);
        return new ApiResponse("success", "Cập nhật quiz thành công!", quiz);
    }

    // Hàm cho user: chỉ cập nhật danh sách câu hỏi
    public ApiResponse updateQuizQuestionsByUser(String id, List<Question> questions) {
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));

        quiz.setQuizQuestions(questions);
        quizRepository.save(quiz);

        return new ApiResponse("success", "Cập nhật câu hỏi thành công!", quiz);
    }

    public ApiResponse deleteQuiz(String id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        if (quiz.isEmpty()) {
            return new ApiResponse("error", "Quiz không tồn tại.", null);
        }

        quizRepository.deleteById(id);
        return new ApiResponse("success", "Xóa quiz thành công!", quiz.get());
    }
}
