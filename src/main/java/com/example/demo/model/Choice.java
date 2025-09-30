package com.example.demo.model;

/**
 * Represents a single choice in a question, including its text, ID, and correctness status.
 */
public class Choice {
    private final Integer choiceID;
    private String choiceText;
    private boolean isCorrect;

    public Choice(int choiceID, String choiceText, boolean isCorrect) {
        this.choiceID = choiceID;
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
    }

    
    // for when creating choices to insert with no ID yet
    public Choice(String choiceText, boolean isCorrect) {
        this.choiceID = null;
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
    }

    /**
     * Preferred accessor with standard casing and explicit null-safety.
     * @return the numeric ID of this choice
     * @throws IllegalStateException if this choice has not been assigned an ID yet
     */
    public int getChoiceID() {
        if (choiceID == null) {
            throw new IllegalStateException("Choice ID is not set for this Choice instance.");
        }
        return choiceID;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}


