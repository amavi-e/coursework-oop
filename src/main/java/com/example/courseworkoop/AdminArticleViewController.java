package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminArticleViewController {
    @FXML
    public Label usernameLabel;
    @FXML
    public Button signInButton;
    @FXML
    public Button signUpButton;
    @FXML
    public Button logOutButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label urlLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button deleteButton;

    private String title;
    private String description;
    private String url;
    private String username;

    //set username and populate articles when set
    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    //set the article details
    public void setArticleDetails(String title, String description, String url, String username) {
        this.title = title;
        this.description = description;
        this.url = url;

        titleLabel.setText(title);
        this.username = username;
        descriptionLabel.setText("Description: " + description);
        urlLabel.setText("URL: " + url);

        setUsername(username);

        //click event on URL
        urlLabel.setOnMouseClicked(this::openArticleUrl);
    }


    @FXML
    public void onBackButtonClick(ActionEvent actionEvent) throws IOException {
        redirectToManageArticlesScreen();
    }


    @FXML
    public void onDeleteArticleButtonClick(ActionEvent actionEvent) {
        if (deleteArticleFromDatabase()) {
            try {
                redirectToManageArticlesScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //delete the article from the database
    public boolean deleteArticleFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/personalizedArticles";
        String user = "root";
        String password = "";

        String deleteQuery = "DELETE FROM Articles WHERE title = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            statement.setString(1, title);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Article deleted successfully: " + title);
                return true;
            } else {
                System.out.println("Failed to delete article: " + title);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //method to redirect to the manage articles screen
    public void redirectToManageArticlesScreen() throws IOException {
        Stage previousStage = (Stage) deleteButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-manage-articles.fxml"));
        Parent root = loader.load();

        AdminManageArticlesController adminManageArticlesController = loader.getController();
        adminManageArticlesController.setUsername(username);

        previousStage.setScene(new Scene(root, 743, 495));
        previousStage.show();
    }

    //method to open the article URL in a browser
    private void openArticleUrl(MouseEvent event) {
        if (url != null && !url.isEmpty()) {
            try {
                //open the URL in the default browser
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSignInButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signInButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("admin-sign-in.fxml"));
        previousStage.setScene(new Scene(root, 331, 400));
        previousStage.show();
    }

    public void onSignUpButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.signUpButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("admin-sign-up.fxml"));
        previousStage.setScene(new Scene(root, 516, 400));
        previousStage.show();
    }

    public void onLogOutButtonClick(ActionEvent actionEvent) throws IOException {
        Stage previousStage = (Stage)this.logOutButton.getScene().getWindow();
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("portal-selection-page.fxml"));
        previousStage.setScene(new Scene(root, 476, 167));
        previousStage.show();
    }
}
