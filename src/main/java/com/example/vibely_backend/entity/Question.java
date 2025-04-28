package com.example.vibely_backend.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Field("mainQuestion")
    private String mainQuestion;

    @Field("choices")
    private List<String> choices;

    @Field("correctAnswer")
    private int correctAnswer;

    @Field("answeredResult")
    private int answeredResult = -1;

    @Field("statistics")
    private Statistics statistics = new Statistics();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistics {
        private int totalAttempts = 0;
        private int correctAttempts = 0;
        private int incorrectAttempts = 0;
    }
}

