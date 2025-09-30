package com.example.demo.controller;

import com.example.demo.model.Session;
import com.example.demo.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button playButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Button instructionButton;

    @FXML
    private Button profileButton;


    public void initialize(){
        welcomeLabel.setText("Welcome, " + Session.getUser().getUsername());
    }

    @FXML
    protected void onLogOutButtonClick() throws IOException {
        Session.clear();
        NavigationManager.goTo( "first-screen-view.fxml");
    }

    //Loads up the reading-view screen
    @FXML
    protected void onPlayButtonClick() { NavigationManager.goTo("reading-view.fxml"); }

    @FXML
    protected void onInstructionButtonClick(){
        NavigationManager.goTo("instruction-view.fxml");
    }

    @FXML
    protected void onProfileButtonClick(){
        NavigationManager.goTo("profile-view.fxml");
    }
}
