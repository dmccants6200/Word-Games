package com.wordgames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MainSceneController {
    public int DIFFICULTY;

    @FXML
    private Button continueButton;

    @FXML
    private BorderPane difficultyMenu;

    @FXML
    private Button easyButton;

    @FXML
    private HBox gameSelectMenu;

    @FXML
    private Button hardButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button playButton;

    @FXML
    private Button playWordLadder;

    @FXML
    private BorderPane titleScreen;

    @FXML
    private Button titleScreenButton;

    @FXML
    private BorderPane wordLadderPane;

    @FXML
    void closeTitleScreen(ActionEvent event) {
        titleScreen.setVisible(false);
        difficultyMenu.setVisible(true);
    }

    @FXML
    void openGameSelect(ActionEvent event) {
        difficultyMenu.setVisible(false);
        gameSelectMenu.setVisible(true);
        continueButton.setText("Please select difficulty");
        continueButton.setDisable(true);
    }

    @FXML
    void openTitleScreen(ActionEvent event) {
        titleScreen.setVisible(true);
        difficultyMenu.setVisible(false);
    }

    @FXML
    void setDifficultyEasy(ActionEvent event) {
        DIFFICULTY = 0;
        continueButton.setDisable(false);
        continueButton.setText("Continue on Easy difficluty");
    }

    @FXML
    void setDifficultyHard(ActionEvent event) {
        DIFFICULTY = 2;
        continueButton.setDisable(false);
        continueButton.setText("Continue on Hard difficluty");
    }

    @FXML
    void setDifficultyMedium(ActionEvent event) {
        DIFFICULTY = 1;
        continueButton.setDisable(false);
        continueButton.setText("Continue on Medium difficluty");
    }

    @FXML
    void startWordLadder(ActionEvent event) {
        //String[] ladder = gameLogic.getWordLadder(DIFFICULTY);


    }
}
