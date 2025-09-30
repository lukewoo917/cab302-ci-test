package com.example.demo.controller;

import com.example.demo.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class AnswerReviewController {

    public TextArea answerReviewText;

    @FXML
    private Button backButton;

    @FXML
    protected void onBackButtonClick(){
        if(NavigationManager.canBack())NavigationManager.back();
        else NavigationManager.goTo("main-view.fxml");
    }
}
