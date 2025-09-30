package com.example.demo;

import com.example.demo.util.NavigationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReadRacerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(ReadRacerApplication.class.getResource("first-screen-view.fxml"));
        Parent firstRoot = loader.load();
        NavigationManager.init(stage, firstRoot, 1000, 600);
    }
    public static void main(String[] args) {
        launch();
    }
}