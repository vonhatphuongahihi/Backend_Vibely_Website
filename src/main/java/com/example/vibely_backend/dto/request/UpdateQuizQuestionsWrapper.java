package com.example.vibely_backend.dto.request;

import java.util.List;

import com.example.vibely_backend.entity.Question;

import lombok.Data;

@Data
public class UpdateQuizQuestionsWrapper {
    private List<Question> updateQuizQuestions;
}
