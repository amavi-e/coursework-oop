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

    /* ^ is the start of the string, a-z0-9_ is a to z or 0 to 9 or underscore,+ ensures that one or more characters
    from the specified set is present, $ is the end of the string */
    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");

    public void onSignUpFinalButtonClick(ActionEvent actionEvent) throws IOException {
        String username = userNameFieldSignUp.getText(); //get username field
        String password = passwordFieldSignUp.getText(); //get password field

        //check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        //validate username format
        if (!isValidUsername(username)) {
            showAlert("Error", "Username must contain only lowercase letters and numbers, without spaces or special characters.");
            return;
        }

        DatabaseManager dbManager = new DatabaseManager();

        //check if username already exists
        if (dbManager.usernameExists(username)) {
            showAlert("Error", "Username already exists. Please choose a different username.");
            return;
        }

        //create a new User object and register it to the system
        User newUser = new User(username, password);
        dbManager.registerUser(newUser.getUsername(), newUser.getPassword());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
        Parent root = loader.load();

        //pass the new User object to UserViewController
        UserViewController userController = loader.getController();
        userController.setUser(newUser);

        Stage stage = (Stage) signUpFinalButton.getScene().getWindow();
        stage.setScene(new Scene(root, 743, 558));
        stage.show();

        //clear fields after successful registration
        userNameFieldSignUp.clear();
        passwordFieldSignUp.clear();
    }

    //go to sign in page
    public void onSignInCreateAccountClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.signInCreateAccountButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("sign-in-page.fxml"));
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
        return VALID_USERNAME_PATTERN.matcher(username).matches(); //can only have simple letters, numbers and underscores
    }
}
