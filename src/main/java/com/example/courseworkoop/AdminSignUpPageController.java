package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class AdminSignUpPageController {
    @FXML
    public TextField userNameFieldSignUp;
    @FXML
    public Button signUpFinalButton;
    @FXML
    public Button signInCreateAccountButton;
    @FXML
    public PasswordField passwordFieldSignUp;
    @FXML
    public TextField fullNameField;

    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[a-z0-9]+$");


    public void onSignUpFinalButtonClick(ActionEvent actionEvent) throws IOException {
        String username = userNameFieldSignUp.getText(); //get the username
        String password = passwordFieldSignUp.getText(); //get the password
        String fullName = fullNameField.getText(); //get the full name

        //check if fields are empty
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        //validate username format
        if (!isValidUsername(username)) {
            showAlert("Error", "Username must contain only lowercase letters and numbers, without spaces or special characters.");
            return;
        }

        DatabaseManager dbManager = new DatabaseManager();


        //check if admin is already registered
        if (dbManager.adminAlreadyRegistered(fullName, username, password)) {
            showAlert("Error", "An account with this full name, username, and password already exists. Please log in.");
            return;
        }
        //check if admin username already exists
        else if (dbManager.adminUsernameExists(username)) {
            showAlert("Error", "Admin username already exists. Please choose a different username.");
            return;
        }

        //register the new admin
        dbManager.registerAdmin(fullName, username, password);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-dashboard.fxml"));
        Parent root = loader.load();

        //pass the username to UserViewController
        AdminDashboardController adminDashboardController = loader.getController();
        adminDashboardController.setUsername(username);

        Stage stage = (Stage) signUpFinalButton.getScene().getWindow();
        stage.setScene(new Scene(root, 743, 165));
        stage.show();

        //clear fields after successful registration
        fullNameField.clear();
        userNameFieldSignUp.clear();
        passwordFieldSignUp.clear();
    }

    public void onSignInCreateAccountClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.signInCreateAccountButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("admin-sign-in.fxml"));
        previousStage.setScene(new Scene(root, 331, 400));
        previousStage.show();
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isValidUsername(String username) {
        return VALID_USERNAME_PATTERN.matcher(username).matches(); //can only have simple letters and numbers
    }
}
