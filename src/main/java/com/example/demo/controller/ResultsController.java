package com.example.demo.controller;

import com.example.demo.ReadRacerApplication;
import com.example.demo.model.*;
import com.example.demo.util.SqliteReadRacerDAO;
import com.example.demo.util.NavigationManager;
import com.example.demo.util.SqliteReadRacerDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ResultsController {

    public Label resultsLabel;

    @FXML
    private Button playAgainButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button reviewAnswerButton;

    @FXML
    private Button leaderboardButton;

    @FXML
    private Label scoreLabel;

    @FXML
    protected void initialize() throws SQLException {
        try {
            QuizResult result = Session.getLastQuizResult();
            if (result != null) {
                scoreLabel.setText("Score: " + result.score());
            } else {
                scoreLabel.setText("No results available");
            }

            // Only store if we actually have a user, reading, and result
            if (Session.getUser() != null &&
                    Session.getCurrentReading() != null &&
                    Session.getLastQuizResult() != null) {
                storeUserAttempt(); // now wrapped to handle its own exceptions
            }
        } catch (Throwable t) {
            // log but do not crash the scene load
            System.err.println("Results init failed: " + t);
            // Optionally show a non-blocking message in UI
        }
    }

    @FXML
    protected void onPlayAgainButtonClick() throws IOException {
        //Clear game state
        Session.clearGameTimer();
        Session.clearLastQuizResult();
        NavigationManager.goTo( "reading-view.fxml");
    }

    @FXML
    protected void onHomeButtonClick()throws IOException {
        //Clear game state
        Session.clearGameTimer();
        Session.clearLastQuizResult();
        NavigationManager.goTo( "main-view.fxml");
    }

    @FXML
    protected void onReviewAnswerButtonClick(){
        NavigationManager.goTo("answer-review-view.fxml");
    }

    @FXML
    protected void onLeaderboardButtonClick(){
        NavigationManager.goTo("leaderboard-view.fxml");
    }


    private void storeUserAttempt() {
        try{
            UserAttempt ua = new UserAttempt(Session.getUser().getUser_id(), Session.getCurrentReading().id(), Session.getLastQuizResult().score());
            Session.DAO.addUserAttempt(ua);
        }catch(SQLException e){
            System.err.println("Failed to store attempt: "+e.getMessage());
        }

    }
}
