package com.example.courseworkoop;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BaseViewController {

    @FXML
    protected Label usernameLabel; // This label will display the username

    // Method to set the username on any view
    public void setUsername(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }
}
