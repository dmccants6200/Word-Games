package com.wordgames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainSceneController {

    @FXML
    private BorderPane difficultyMenu;

    @FXML
    private Button easyButton;

    @FXML
    private Button hardButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button playButton;

    @FXML
    private BorderPane titleScreen;

    @FXML
    private Button titleScreenButton;

    @FXML
    void closeTitleScreen(ActionEvent event) {
        titleScreen.setVisible(false);
        difficultyMenu.setVisible(true);
    }

    @FXML
    void openTitleScreen(ActionEvent event) {
        titleScreen.setVisible(true);
        difficultyMenu.setVisible(false);
    }

    @FXML
    void setDifficulty(ActionEvent event) {

    }

}