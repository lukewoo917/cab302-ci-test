package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Question object for a reading.
 * @param prompt The prompt for the question
 * @param choices The list of choices for the question
 */
public record Question(String prompt, List<Choice> choices) {
    /**
     * Constructor for questions with choices.
     */
    public Question(String prompt, List<Choice> choices) {
        this.prompt = prompt;
        this.choices = choices != null ? new ArrayList<>(choices) : new ArrayList<>();
        //validates that there is only one correct answer
        if (choices != null) {
            long correctCount = choices.stream().filter(Choice::isCorrect).count();
            if (correctCount != 1) {
                choices.forEach(c -> c.setCorrect(false));
                if (!choices.isEmpty()) {
                    choices.getFirst().setCorrect(true);
                }
            }
        }
    }
    
    public Choice getCorrectChoice() {
        return choices.stream()
                .filter(Choice::isCorrect)
                .findFirst()
                .orElse(null);
    }
    
    public String getCorrectAnswerString() {
        return getCorrectChoice().getChoiceText();
    }

    public int getCorrectAnswerID() {
        return getCorrectChoice().getChoiceID();
    }

    /**
     * Checks if the user's answer is correct.
     * @param userAnswer A string representing the user's answer
     * @return true if the user's answer is correct, false otherwise
     */
    public boolean isCorrect(String userAnswer) {
        return getCorrectAnswerString().equals(userAnswer);
    }
}
