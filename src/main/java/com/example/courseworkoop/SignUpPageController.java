package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class SignUpPageController {
    @FXML
    public Button signUpFinalButton;
    @FXML
    public Button signInCreateAccountButton;
    @FXML
    public TextField userNameFieldSignUp;
    @FXML
    public TextField passwordFieldSignUp;

    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[a-z0-9]+$");

    public void onSignUpFinalButtonClick(ActionEvent actionEvent) throws IOException {
        String username = userNameFieldSignUp.getText();
        String password = passwordFieldSignUp.getText();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        // Validate username format
        if (!isValidUsername(username)) {
            showAlert("Error", "Username must contain only lowercase letters and numbers, without spaces or special characters.");
            return;
        }

        DatabaseManager dbManager = new DatabaseManager();

        // Check if username already exists
        if (dbManager.usernameExists(username)) {
            showAlert("Error", "Username already exists. Please choose a different username.");
            return;
        }

        // Register the new user
        dbManager.registerUser(username, password);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
        Parent root = loader.load();

        // Pass the username to UserViewController
        UserViewController userController = loader.getController();
        userController.setUsername(username);

        Stage stage = (Stage) signUpFinalButton.getScene().getWindow();
        stage.setScene(new Scene(root, 648, 356));
        stage.show();


        // Clear fields after successful registration
        userNameFieldSignUp.clear();
        passwordFieldSignUp.clear();
    }

    public void onSignInCreateAccountClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.signInCreateAccountButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("sign-in-page.fxml"));
        previousStage.setScene(new Scene(root, 331, 400));
        previousStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidUsername(String username) {
        return VALID_USERNAME_PATTERN.matcher(username).matches(); //can only have simple letters and numbers
    }
}
