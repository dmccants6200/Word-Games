package com.wordgames;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
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
    void startWordLadder(ActionEvent event) throws IOException{
        FXMLLoader wordLadderUI = new FXMLLoader(App.class.getResource("wordLadder.fxml"));
        Parent root = wordLadderUI.load();
        Scene scene = new Scene(root, 640, 480);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.hide();
        stage.setTitle("Word Games: Word Ladder");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}
