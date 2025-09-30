package com.example.demo.model;
import com.example.demo.util.SqliteReadRacerDAO;
import com.example.demo.util.CountdownTimer;

/**
 * Session object for a user's session.
 */
public class Session {
    private static User currentUser;

    private static Reading currentReading;

    private static QuizResult lastQuizResult;

    private static CountdownTimer gameTimer;

    public static SqliteReadRacerDAO DAO = new SqliteReadRacerDAO();

    /**
     * Sets the current reading for the user.
     * @param currentReading accepts a reading object and sets it as the current reading.
     */
    public static void setCurrentReading(Reading currentReading) {
        Session.currentReading = currentReading;
    }

    /**
     * Gets the current reading for the user.
     * @return current reading as a reading object.
     */
    public static Reading getCurrentReading() {
        return currentReading;
    }

    /**
     * Sets the current user.
     * @param user accepts a user object and sets it as the current user.
     */
    public static void setUser(User user) {
        currentUser = user;
    }

    /**
     * Gets the current user.
     * @return logged-in user as a user object.
     */
    public static User getUser() {
        return currentUser;
    }

    /**
     * Gets the last quiz result.
     * @return quiz results as QuizResult object.
     */
    public static QuizResult getLastQuizResult() {
        return lastQuizResult;
    }

    /**
     * Sets the new quiz results
     * @param lastQuizResult accepts QuizResult object and sets it to quizResult
     */
    public static void setLastQuizResult(QuizResult lastQuizResult) {
        Session.lastQuizResult = lastQuizResult;
    }

    /**
     * Clears the last quiz result.
     */
    public static void clearLastQuizResult(){
        Session.lastQuizResult=null;
    }
    /**
     * Clears the session.
     */
    public static void clear() {
        currentUser = null;
        currentReading = null;
        lastQuizResult=null;
        gameTimer=null;
    }


    // Shared timer for Reading and Question

    public static CountdownTimer getGameTimer() {
        return gameTimer;
    }

    public static void setGameTimer(CountdownTimer countdownTimer){
        gameTimer=countdownTimer;
    }
    public static void clearGameTimer(){
        gameTimer=null;
    }

}