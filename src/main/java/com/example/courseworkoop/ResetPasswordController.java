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

public class ResetPasswordController {
    @FXML
    public TextField usernameResetPasswordField;
    @FXML
    public PasswordField enterNewPasswordField;
    @FXML
    public PasswordField confirmNewPasswordField;
    @FXML
    public Button resetPasswordButton;

    public void onResetPasswordButtonClick(ActionEvent actionEvent) throws IOException {
        String username = usernameResetPasswordField.getText(); //get username from field
        String newPassword = enterNewPasswordField.getText(); //get new password from field
        String confirmPassword = confirmNewPasswordField.getText(); //get confirm password from field

        //check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        //create a User object with the provided username and new password
        User user = new User(username, newPassword);

        //validate username and update password
        DatabaseManager dbManager = new DatabaseManager();
        if (dbManager.usernameExists(user.getUsername())) {
            boolean isUpdated = dbManager.updatePassword(user.getUsername(), user.getPassword());
            if (isUpdated) {
                showAlert("Success", "Password updated successfully.");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
                Parent root = loader.load();

                //pass the User object to UserViewController
                UserViewController userController = loader.getController();
                userController.setUser(user);

                Stage stage = (Stage) resetPasswordButton.getScene().getWindow();
                stage.setScene(new Scene(root, 743, 558));
                stage.show();
            } else {
                showAlert("Error", "Failed to update password. Please try again.");
            }
        } else {
            showAlert("Error", "Username not found.");
        }
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
