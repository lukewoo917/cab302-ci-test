package com.example.demo.controller;

import com.example.demo.ReadRacerApplication;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RegistrationController {
    @FXML
    public TextField usernameField;
    @FXML
    public TextField emailField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public Label errorLabel;
    public Label instructionText;
    public Label welcomeText;
    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;


    @FXML
    protected void onCancelButtonClick(ActionEvent actionEvent) throws IOException {
        NavigationManager.goTo( "first-screen-view.fxml");
    }

    @FXML
    private void HandleRegister() {

        final String username = usernameField.getText();
        final String rawEmail = emailField.getText();
        final String email = rawEmail == null ? "" : rawEmail.trim();
        final String password = passwordField.getText();
        final String confirmPassword = confirmPasswordField.getText();

        // Collect all errors
        StringBuilder errorMessages = new StringBuilder();

        Optional<List<String>> usernameError = User.verifyUsername(username);
        usernameError.ifPresent(err -> errorMessages.append(err).append("\n"));

        Optional<List<String>> passwordError = User.verifyPassword(password);
        passwordError.ifPresent(err -> errorMessages.append(err).append("\n"));

        if (!password.equals(confirmPassword)) {
            errorMessages.append("Passwords do not match\n");
        }

        //check for unique username and email
        try {
            if (!Session.DAO.usernameIsUnique(username)) {
                errorMessages.append("Username is already taken\n");
            }
            if (!Session.DAO.emailIsUnique(email)) {
                    errorMessages.append("Email is already taken\n");
                }
        } catch (SQLException e) {
            errorMessages.append(e.getMessage());
        }

        // Check if we have errors to ensure any logic that needs to happen with verification happens before this
        if (!errorMessages.isEmpty()) {
            showError(errorMessages.toString().trim());
            return;
        }


        // password hashing with md5, Not recommended for production but good enough for now.
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        // todo: confirmation screen?
        clearError();
        User user = new User(username, email, hashedPassword, salt);

        try {
            Session.DAO.addUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }

        Session.setUser(user);
        NavigationManager.goTo("main-view.fxml");
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


    @FXML
    protected void onConfirmButtonClick(){
        HandleRegister();
    }
}
