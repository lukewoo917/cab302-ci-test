package com.example.demo.controller;

import com.example.demo.ReadRacerApplication;
import com.example.demo.model.Reading;
import com.example.demo.model.Session;
import com.example.demo.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import com.example.demo.util.CountdownTimer;
import com.example.demo.exceptions.TimerException;

import java.io.IOException;

public class ReadingController {

    public Label readingLabel;
    @FXML
    private Button exitButton;

    @FXML
    private Button finishButton;

    @FXML
    private TextArea passageText;


    // Countdown timer variables
    @FXML
    private Label timerLabel;

    private CountdownTimer timer;
    private boolean jumpedToQuestions = false;


    @FXML
    protected void initialize(){
        //get random reading
        Reading randomReading = Session.DAO.getRandomReading();

        // assign reading to session
        Session.setCurrentReading(randomReading);

        // Load reading passage
        passageText.setText(Session.getCurrentReading().passage());
        passageText.setEditable(false);  // Makes the TextArea read-only

        // Start 4-min timer
        createNewGameTimer();

        //Attach reading-phase callbacks
        attachTimerCallBacks();

        // Show current remaining immediately
        timerLabel.setText(formatMMSS(timer.getRemainingSeconds()));

    }

    @FXML
    protected void onExitButtonClick()throws IOException {
        // Leaving the flow entirely: stop and clear the shared timer
        jumpedToQuestions = true;
        Session.clearGameTimer();
        NavigationManager.goTo( "main-view.fxml");
    }

    @FXML
    protected void onFinishButtonClick()throws IOException {
        jumpedToQuestions = true; // user moved early; suppress auto-jump
        goToQuestionsPhase();
    }

    @FXML
    private void goToQuestionsPhase() {
        // Do NOT stop the timer here; we want the remaining time to carry over
        NavigationManager.goTo("questions-view.fxml");
    }

    @FXML
    private void createNewGameTimer(){
        timer = new CountdownTimer(
                240, // 4 minutes shared across phases
                null, // controllers will attach UI later
                null
        );

        timer.setRate(1.0);

        if (!timer.isRunning())
        {
            timer.start();
        }

        Session.setGameTimer(timer);
    }

    private void attachTimerCallBacks(){
        // Update label + auto-jump if still on Reading when <= 60s remain
        timer.setOnTick(secs -> {
            timerLabel.setText(formatMMSS(secs));
            if (secs <= 70 && secs > 60) {
                timerLabel.setStyle("-fx-text-fill:#d90429; -fx-font-weight:bold;");
            } else {
                timerLabel.setStyle(""); // reset
            }

            if (!jumpedToQuestions && secs <= 60) {
                jumpedToQuestions = true;
                goToQuestionsPhase(); // move to Questions after 3 minutes
            }
        });


        // Hard fallback when time hits 0 (should rarely happen on Reading):
        timer.setOnFinished(() -> {
            // If somehow still here at 0, just go questions (questions will immediately finish to results)
            if (!jumpedToQuestions) {
                jumpedToQuestions = true;
                goToQuestionsPhase();
            }
        });
    }


    private String formatMMSS(int totalSecs) {
        int m = totalSecs / 60;
        int s = totalSecs % 60;
        return String.format("%02d:%02d", m, s);
    }
}