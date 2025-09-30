package com.example.demo.controller;

import com.example.demo.ReadRacerApplication;
import com.example.demo.exceptions.InvalidCredentialsException;
import com.example.demo.model.Session;
import com.example.demo.model.User;
import com.example.demo.util.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {


    public PasswordField passwordField;
    public TextField usernameField;
    public Label errorLabel;
    public Label welcomeText;
    public Label loginText;

    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button signUpButton;


    @FXML
    protected void onLoginButtonClick() throws IOException {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
        }

        User user;
        try {
            user = Session.DAO.TryLogin(username, password);
            if (user == null) {
                showError("No user found");
            } else {
                Session.setUser(user);
                NavigationManager.goTo("main-view.fxml");
            }
        } catch (InvalidCredentialsException e) {
            showError("Invalid username or password");
            return;
        } catch (SQLException e) {
            showError(e.getMessage());
            // (optional) log e
            return;
        }
    }

    private void showError(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg);
        }
    }

    private void clearError() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    public void onCancelButtonClick() {
        if (NavigationManager.canBack()) {
            NavigationManager.back();
        } else {
            NavigationManager.goTo("first-screen-view.fxml");
        }
    }
}
