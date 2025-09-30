package com.example.demo.controller;

import com.example.demo.ReadRacerApplication;
import com.example.demo.exceptions.NavigationException;
import com.example.demo.exceptions.ScoringException;
import com.example.demo.model.*;
import com.example.demo.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.demo.util.CountdownTimer;
import com.example.demo.exceptions.TimerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsController {

    public Label questionsLabel;
    // Countdown timer variables
    @FXML
    private Label timerLabel;

    private CountdownTimer timer;

    @FXML
    private Button submitButton;

    @FXML
    private VBox questionsContainer;

    private final List<ToggleGroup> groups = new ArrayList<>();

    private List<Question> questions;

    private boolean finishing = false;

    @FXML
    protected void onSubmitButtonClick() {
        finaliseQuiz(retrieveUserAnswersSafe());

    }


    @FXML
    protected void initialize() {
        // Validate session state early
        if (!ensureReadingPresent()) return;

        //capture the questions
        questions = Session.getCurrentReading().questions();
        if(!ensureQuestionsPresent())return;

        //Retrieve the timer
        timer = Session.getGameTimer();

        // If timer is missing or already finished, finalise with whatever is selected (likely none)
        if (timer == null || timer.getRemainingSeconds() <= 0) {
            finaliseQuiz(retrieveUserAnswersSafe());
            return;
        }

        // 1) Build the questions UI FIRST so groups exist before the timer finishes
        buildQuestionsUI();

        // 2) Timer callbacks with protection
        attachTimerCallbacks(timer);

        // Show current remaining immediately
        timerLabel.setText(formatMMSS(timer.getRemainingSeconds()));
    }


    private void buildQuestionsUI() {
        //Iterate through the questions
        for (Question q : questions) {

            //Container for individual question box
            VBox questionBox = new VBox();

            //Add the prompt label
            Label prompt = new Label(q.prompt());
            prompt.getStyleClass().add("questions");
            questionBox.getChildren().add(prompt);

            //VBOX container for radio buttons
            VBox answersBox = new VBox();

            // Group the choices so only one can be selected
            ToggleGroup group = new ToggleGroup();

            for (Choice choice : q.choices()) {
                RadioButton rb = new RadioButton(choice.getChoiceText());
                // attaching the choice object to the button to easily retrieve it later
                rb.setUserData(choice);
                rb.getStyleClass().add("answers");
                rb.setToggleGroup(group);
                answersBox.getChildren().add(rb);
            }

            // Collect user info
            groups.add(group);

            // Append to fxml file elements
            questionBox.getChildren().add(answersBox);
            questionsContainer.getChildren().add(questionBox);
        }
    }

    private void attachTimerCallbacks(CountdownTimer t){
        t.setOnTick(secs -> {
            try {
                timerLabel.setText(formatMMSS(secs));
                if (secs <= 10) {
                    timerLabel.setStyle("-fx-text-fill:#d90429; -fx-font-weight:bold;");
                } else {
                    timerLabel.setStyle("");
                }
            } catch (Throwable e) {
                // defensive: don't let rendering issues crash FX thread
                System.err.println("Tick handler failed: " + e.getMessage());
            }
        });

        timer.setOnFinished(() -> {
            try {
                if (finishing) return;
                finishing = true;
                finaliseQuiz(retrieveUserAnswersSafe());
            } catch (Throwable e) {
                showError("Timer Finished", "Unable to finalize answers: " + e.getMessage());
                safeGoToMain();
            }
        });
    }

    /** Collects current selections; returns null for unanswered */
    private List<Choice> retrieveUserAnswersSafe() {
        List<Choice> answers = new ArrayList<>(questions.size());
        if (groups.isEmpty()) {
            // UI not built (defensive) → return all blanks
            for (int i = 0; i < questions.size(); i++) answers.add(null);
            return answers;
        }
        for (ToggleGroup group : groups) {
            Toggle selected = group.getSelectedToggle();
            // if unanswered, add a null else add the selected choice
            answers.add(selected==null?null:(Choice) selected.getUserData());
        }
        return answers;
    }

    private void finaliseQuiz(List<Choice> rawAnswers){
        try{
            List<Choice> answers = normaliseAnswers(rawAnswers,questions.size());
            QuizResult result= computeResult(answers,questions,safeGetRemainingSeconds());
            Session.setLastQuizResult(result);
            navigateToResults();
        }catch(ScoringException | NavigationException e){
            showError("Error",e.getMessage());
            safeGoToMain();
        }catch(Throwable t){
            showError("Unexpected Error",t.toString());
        }
    }


    private List<Choice> normaliseAnswers(List<Choice> answers,int expectedSize){
        if(answers==null){
            List<Choice> blanks= new ArrayList<>(expectedSize);
            for (int i = 0; i<expectedSize;i++)blanks.add(null);
            return blanks;
        }
        if(answers.size()!=expectedSize){
            throw new ScoringException("Collected answers do not match questions count.");
        }
        return answers;
    }

    private QuizResult computeResult(List<Choice> answers, List<Question> questions, int remainingSeconds){
        return ScoringSystem.score(answers,questions,remainingSeconds);
    }

    private int safeGetRemainingSeconds(){
        return (timer==null)?0:Math.max(0, timer.getRemainingSeconds());
    }

    private void navigateToResults(){
        try {
            NavigationManager.goTo("results-view.fxml");
        } catch (RuntimeException navErr) {
            throw new NavigationException("Failed to navigate to results", navErr);
        }
    }


    private void safeGoToMain() {
        try {
            Session.clearGameTimer();
        } catch (Throwable ignored) {}
        try {
            NavigationManager.goTo("main-view.fxml");
        } catch (Throwable ignored) {}
    }


    private boolean ensureReadingPresent(){
        if (Session.getCurrentReading() != null) return true;
        showError("Missing", "No active reading found.Returning to Main.");
        safeGoToMain();
        return false;
    }

    private boolean ensureQuestionsPresent(){
        if (questions.isEmpty()) {
            showError("No Questions", "This reading has no questions. Returning to main.");
            safeGoToMain();
            return false;
        }
        return true;
    }

    private void showError(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ReadRacer");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.show();
    }

    private String formatMMSS(int totalSecs) {
        int m = totalSecs / 60;
        int s = totalSecs % 60;
        return String.format("%01d:%02d", m, s);
    }


}
