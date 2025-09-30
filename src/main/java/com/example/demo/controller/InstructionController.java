package com.example.demo.controller;

import com.example.demo.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class InstructionController {

    @FXML
    private Button backButton;

    @FXML
    protected void onBackButtonClick(){
        if(NavigationManager.canBack())NavigationManager.back();
        else NavigationManager.goTo("main-view.fxml");
    }
}
