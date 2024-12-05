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

public class SignInPageController {
    @FXML
    public Button signInButtonFinal;
    @FXML
    public Button signUpLoginPageButton;
    @FXML
    public TextField usernameSignInPage;
    @FXML
    public PasswordField passwordSignInPage;
    @FXML
    public Button forgotPasswordButton;

    public void OnSignInButtonFinalClick(ActionEvent actionEvent) throws IOException {
        String username = usernameSignInPage.getText(); //get username field
        String password = passwordSignInPage.getText(); //get password field

        //check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        //create a User object for validation
        User user = new User(username, password);

        //validate the username and password with the database
        DatabaseManager dbManager = new DatabaseManager();
        boolean validUser = dbManager.validateUser(user.getUsername(), user.getPassword());

        if (validUser) { //if user is valid, open home page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
            Parent root = loader.load();

            //pass the User object to UserViewController
            UserViewController userController = loader.getController();
            userController.setUser(user);

            Stage stage = (Stage) signInButtonFinal.getScene().getWindow();
            stage.setScene(new Scene(root, 743, 558));
            stage.show();
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    //showing alerts method
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //go to sign up page
    public void onSignUpLoginPageButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.signUpLoginPageButton.getScene().getWindow();
        Parent root = FXMLLoader.load(this.getClass().getResource("sign-up-page.fxml"));
        previousStage.setScene(new Scene(root, 516, 400));
        previousStage.show();
    }

    //open ResetPasswordController if clicked on forgot password
    public void onForgotPasswordButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage) this.forgotPasswordButton.getScene().getWindow();
        Parent root = FXMLLoader.load(this.getClass().getResource("reset-password.fxml"));
        previousStage.setScene(new Scene(root, 482, 400));
        previousStage.show();
    }
}
