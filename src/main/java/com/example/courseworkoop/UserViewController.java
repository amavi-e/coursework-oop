package com.example.courseworkoop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UserViewController {
    @FXML
    public Button technologyCategoryButton;
    @FXML
    public Button healthCategoryButton;
    @FXML
    public Button sportsCategoryButton;
    @FXML
    public Button aiCategoryButton;
    @FXML
    public Button businessCategoryButton;
    @FXML
    public Button scienceCategoryButton;
    @FXML
    public Button entertainmentCategoryButton;
    @FXML
    public Button educationCategoryButton;
    @FXML
    public Button environmentCategoryButton;
    @FXML
    public Button recommendArticlesButton;
    @FXML
    public Label usernameLabel; // Display username if needed

    private String username;

    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    public void onTechnologyButtonClick(ActionEvent actionEvent) {
    }

    public void onCategoryButtonClick(ActionEvent actionEvent) {
    }

    public void onSportsButtonClick(ActionEvent actionEvent) {
    }

    public void onAIButtonClick(ActionEvent actionEvent) {
    }

    public void onBusinessButtonClick(ActionEvent actionEvent) {
    }

    public void onScienceButtonClick(ActionEvent actionEvent) {
    }

    public void onEntertainmentButtonClick(ActionEvent actionEvent) {
    }

    public void onEducationButtonClick(ActionEvent actionEvent) {
    }

    public void onEnvironmentButtonClick(ActionEvent actionEvent) {
    }

    public void onRecommendArticleButtonClick(ActionEvent actionEvent) {
    }
}
