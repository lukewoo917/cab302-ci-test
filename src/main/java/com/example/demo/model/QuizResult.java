package com.example.demo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record QuizResult(int score, int totalQuestions, int questionsAnswered, List<Question> questions, List<Choice> userAnswers,
                         List<String> perQuestionResult) {
    public QuizResult(
            int score,
            int totalQuestions,
            int questionsAnswered,
            List<Question> questions,
            List<Choice> userAnswers,
            List<String> perQuestionResult
    ) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.questionsAnswered = questionsAnswered;
        this.questions = List.copyOf(questions);
        this.userAnswers = userAnswers;
        this.perQuestionResult = List.copyOf(perQuestionResult);
    }

}
