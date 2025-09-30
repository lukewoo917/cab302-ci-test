package com.example.demo.controller;

import com.example.demo.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class WelcomeScreenController {

    public Label welcomeLabel;
    @FXML
    private Button loginBtn;

    @FXML
    private Button signupBtn;

    @FXML
    protected void onLoginBtnClick(){
        NavigationManager.goTo("login-view.fxml");
    }

    @FXML
    protected void onSignupBtnClick(){
        NavigationManager.goTo("registration-view.fxml");
    }
}
