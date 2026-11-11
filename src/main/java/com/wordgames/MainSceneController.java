package com.wordgames;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MainSceneController {
    public int DIFFICULTY;

    @FXML
    private Button continueButton, playButton, playWordLadder, titleScreenButton;

    @FXML
    private BorderPane difficultyMenu, titleScreen, wordLadderPane;

    @FXML
    private HBox gameSelectMenu;

    @FXML
    private RadioButton easyButton, mediumButton, hardButton;

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
    void setDifficulty(ActionEvent event) {
        if (easyButton.isSelected()) {
            continueButton.setText("Continue on Easy");
            DIFFICULTY = 0;
        } else if (mediumButton.isSelected()) {
            continueButton.setText("Continue on Medium");
            DIFFICULTY = 1;
        } else if (hardButton.isSelected()) {
            continueButton.setText("Continue on Hard");
            DIFFICULTY = 2;
        }
    }

    @FXML
    void startWordLadder(ActionEvent event) throws IOException{
        FXMLLoader wordLadderUI = new FXMLLoader(App.class.getResource("wordLadder.fxml"));
        Scene scene = new Scene(wordLadderUI.load(), 640, 480);

        WordLadderController ladderController = wordLadderUI.getController();
        ladderController.initializeWordLadder(DIFFICULTY);

        scene.setOnKeyPressed(keyEvent -> {
            if (!(keyEvent.getCode().isLetterKey() || keyEvent.getCode().toString().equals("BACK_SPACE"))) return;

            ladderController.insertLetter(keyEvent.getCode().toString());
        });

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.hide();
        stage.setTitle("Word Games: Word Ladder");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}
