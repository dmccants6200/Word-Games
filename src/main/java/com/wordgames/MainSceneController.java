package com.wordgames;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;

public class MainSceneController {
    public int DIFFICULTY;

    @FXML
    private Button continueButton, playButton, playWordLadder, playJumble, titleScreenButton, difficultyButton;

    @FXML
    private BorderPane difficultyMenu, titleScreen, wordLadderPane, gameSelectMenu;

    @FXML
    private RadioButton easyButton, mediumButton, hardButton;

    @FXML
    void closeTitleScreen(ActionEvent event) {
        titleScreen.setVisible(false);
        difficultyMenu.setVisible(true);
    }

    @FXML
    void openGameSelect(ActionEvent event) {
        DIFFICULTY = easyButton.isSelected() ? 0 : (mediumButton.isSelected() ? 1 : 2);

        difficultyMenu.setVisible(false);
        gameSelectMenu.setVisible(true);
    }

    @FXML
    void openTitleScreen(ActionEvent event) {
        titleScreen.setVisible(true);
        difficultyMenu.setVisible(false);
    }

    @FXML
    void openDifficultySelect(ActionEvent event) {
        gameSelectMenu.setVisible(false);
        difficultyMenu.setVisible(true);
    }

    @FXML
    void setDifficulty(ActionEvent event) {
        continueButton.setText("Continue on " + (easyButton.isSelected() ? "Easy" : (mediumButton.isSelected() ? "Medium" : "Hard")));
    }

    @FXML
    void startWordLadder(ActionEvent event) throws IOException {
        FXMLLoader wordLadderUI = new FXMLLoader(App.class.getResource("wordLadder.fxml"));
        double x = ((Node)event.getSource()).getScene().getWidth(), y = ((Node)event.getSource()).getScene().getHeight();
        
        Scene scene = new Scene(wordLadderUI.load(), x, y);

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(45);

        scene.setCamera(camera);

        WordLadderController ladderController = wordLadderUI.getController();
        ladderController.initialize();
        ladderController.setDifficulty(DIFFICULTY);
        ladderController.initializeWordLadder();

        scene.setOnKeyPressed(keyEvent -> {
            if (!(keyEvent.getCode().isLetterKey() || keyEvent.getCode().toString().equals("BACK_SPACE"))) return;

            ladderController.insertLetter(keyEvent.getCode().toString());
        });

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Boolean fullscreen = stage.isFullScreen();
        stage.setTitle("Word Games: Word Ladder");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(fullscreen);
        stage.show();
    }

    @FXML
    void startJumble(ActionEvent event) throws IOException {
        FXMLLoader jumbleUI = new FXMLLoader(App.class.getResource("jumble.fxml"));
        double x = ((Node)event.getSource()).getScene().getWidth(), y = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(jumbleUI.load(), x, y);

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(45);

        scene.setCamera(camera);

        JumbleController jumbleController = jumbleUI.getController();
        jumbleController.initialize();
        jumbleController.setDifficulty(DIFFICULTY);
        jumbleController.initializeJumble();

        scene.setOnKeyPressed(keyEvent -> {
            if (!(keyEvent.getCode().isLetterKey() || keyEvent.getCode().toString().equals("BACK_SPACE") || keyEvent.getCode().isArrowKey())) return;
            
            jumbleController.insertLetter(keyEvent.getCode().toString());
        });

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Boolean fullscreen = stage.isFullScreen();
        stage.setTitle("Word Games: Jumble");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(fullscreen);
        stage.show();
    }
}
