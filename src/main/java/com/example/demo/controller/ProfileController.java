package com.example.demo.controller;

import com.example.demo.util.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.example.demo.model.Session;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.sql.SQLException;
import java.util.Arrays;

public class ProfileController {


    public TextField usernameField;
    public Button changeUsernameButton;
    public TextField emailField;
    public Button changeEmailButton;
    public TextArea passwordHashArea;
    public TextArea saltArea;
    public TextArea IDArea;
    @FXML
    private Button usernameChangeButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        usernameField.setText(Session.getUser().getUsername());
        emailField.setText(Session.getUser().getEmail());
        passwordHashArea.setText(Arrays.toString(Session.getUser().getPassword()));
        saltArea.setText(Arrays.toString(Session.getUser().getSalt()));
        IDArea.setText(String.valueOf(Session.getUser().getUser_id()));

    }

    @FXML
    protected void onBackButtonClick() {
     NavigationManager.goTo("main-view.fxml");
    }

    public void onChangeUsernameClick() {
        if (usernameField.isEditable()) {
            // Save and lock
            usernameField.setEditable(false);
            changeUsernameButton.setText("Edit");

            Session.getUser().setUsername(usernameField.getText());
            //todo proper error handling here and username validation, maybe reuse registration logic somehow
            try {
                Session.DAO.updateUser(Session.getUser());
                System.out.println("Updated user" + Session.getUser().getUsername());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            // Unlock for editing
            usernameField.setEditable(true);
            changeUsernameButton.setText("Save");
        }
    }

    public void onChangeEmailClick(ActionEvent actionEvent) {
        if (emailField.isEditable()) {
            // Save and lock
            emailField.setEditable(false);
            changeEmailButton.setText("Edit");

            Session.getUser().setEmail(emailField.getText());
            //todo proper error handling here and username validation, maybe reuse registration logic somehow
            try {
                Session.DAO.updateUser(Session.getUser());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            // Unlock for editing
            emailField.setEditable(true);
            changeEmailButton.setText("Save");
        }
    }

    public void onDeleteButtonClick() {
        try {
            Session.DAO.deleteUser(Session.getUser().getUser_id());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            Session.clear();
            NavigationManager.goTo("first-screen-view.fxml");
        }
    }
}
