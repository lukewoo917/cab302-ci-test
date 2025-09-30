package com.example.demo.model;

public class UserAttempt {
    private Integer attemptID;
    private Integer userID;
    private Integer readingID;
    private Integer score;

    public UserAttempt(int attemptID, int userID, int readingID, int score) {
        this.attemptID = attemptID;
        this.userID = userID;
        this.readingID = readingID;
        this.score = score;
    }

    public UserAttempt(int userID, int readingID, int score) {
        this.userID = userID;
        this.readingID = readingID;
        this.score = score;
    }

    public int getAttemptID() {
        if (attemptID == null) {
            throw new IllegalStateException("Attempt ID is not set for this User Attempt instance.");
        }
        return attemptID;
    }

    public int getUserID() {
        if (userID == null) {
            throw new IllegalStateException("User ID is not set for this User Attempt instance.");
        }
        return userID;
    }

    public int getReadingID() {
        if (readingID == null) {
            throw new IllegalStateException("Reading ID is not set for this User Attempt instance");
        }
        return readingID;
    }

    public int getScore() {
        if (score == null) {
            return 0;
        }
        return score;
    }

    public void setAttemptID(int generatedId) {
        attemptID = generatedId;
    }
}
