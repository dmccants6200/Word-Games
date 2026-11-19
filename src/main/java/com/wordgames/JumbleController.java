package com.wordgames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class JumbleController {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @FXML
    private VBox jumbleContainer;

    @FXML
    private Label clueLabel;

    @FXML
    private GridPane clueGrid;

    @FXML
    private DialogPane winDialog;

    @FXML
    private Button menuButton;

    @FXML
    void exitToMenu(ActionEvent event) throws IOException {
        if (initializing) return;
        FXMLLoader mainUI = new FXMLLoader(App.class.getResource("UI.fxml"));
        Scene scene = new Scene(mainUI.load(), 640, 480);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.hide();
        stage.setTitle("Word Games");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    Boolean initializing = false;
    Boolean won = false;
    private int selected = 0;
    private int DIFFICULTY;
    private int WORD_LENGTH;
    String[] wordList = null;
    String correctAnswer;

    public void setDifficulty(int difficulty) {
        DIFFICULTY = difficulty;
        WORD_LENGTH = difficulty + 3;
    }

    public int getDifficulty() {
        return DIFFICULTY;
    }

    @FXML
    public void initialize() {
        Button nextButton = (Button) winDialog.lookupButton(ButtonType.NEXT);
        
        nextButton.setOnAction(event -> initializeJumble());
    }

    private Label getBox(int index) {
        if (index / WORD_LENGTH <= WORD_LENGTH - 1) {
            GridPane grid = (GridPane)(Node)jumbleContainer.getChildren().get((index / WORD_LENGTH) * 2 + 1);
            
            return (Label)(Node)grid.getChildren().get((index % WORD_LENGTH) * 2);
        } else
            return (Label)(Node)clueGrid.getChildren().get((index % WORD_LENGTH) * 2);
    }

    private String getCircled() {
        String circledLetters = "";

        for (int i = 0; i < WORD_LENGTH * WORD_LENGTH; i++) {
            GridPane grid = (GridPane)(Node)jumbleContainer.getChildren().get((i / WORD_LENGTH) * 2 + 1);
            Circle circle = (Circle)(Node)grid.getChildren().get((i % WORD_LENGTH) * 2 + 1);

            if (!circle.isVisible()) continue;
            
            circledLetters += getBox(i).getText();
        }

        return circledLetters.toLowerCase();
    }

    private Boolean allFilled() {
        Boolean filled = true;

        for (int i = 0; i < WORD_LENGTH * WORD_LENGTH; i++)
            if (getBox(i).getText().equals("")) {
                filled = false;
                break;
            }

        return filled;
    }

    private void checkClue() {
        String scrambledWord = getCircled();
        Set<String> set = new HashSet<>(Arrays.asList(wordList));
        String fullWord = "";
        Boolean invalid = false;

        for (int i = 0; i < WORD_LENGTH * WORD_LENGTH; i++) {
            if (!getBox(i).getStyleClass().contains("invalid")) continue;

            invalid = true; break;
        }

        for (int i = 0; i < (clueGrid.getChildren().size() / 2); i++) {
            Label box = (Label)(Node)clueGrid.getChildren().get(i * 2);
            fullWord += box.getText().toLowerCase();
        }

        if ((!(set.contains(fullWord) && Jumble.areAnagrams(scrambledWord, fullWord)) || invalid) && allFilled() && fullWord.length() == WORD_LENGTH) {
            for (int i = 0; i < (clueGrid.getChildren().size() / 2); i++) {
                Label box = (Label)(Node)clueGrid.getChildren().get(i * 2);
                if(!box.getStyleClass().contains("invalid")) box.getStyleClass().add("invalid");
            }

            return;
        }

        for (int i = 0; i < (clueGrid.getChildren().size() / 2); i++) {
            Label box = (Label)(Node)clueGrid.getChildren().get(i * 2);
            box.getStyleClass().remove("invalid");
        }
    }

    private void checkGame() {
        for (int i = 0; i < WORD_LENGTH * (WORD_LENGTH + 1); i++)
            if (getBox(i).getStyleClass().contains("invalid")) return;
        
        String typedClue = "";

        for (int i = 0; i < (clueGrid.getChildren().size() / 2); i++) {
            Label box = (Label)(Node)clueGrid.getChildren().get(i * 2);
            typedClue += box.getText().toLowerCase();
        }

        if (!typedClue.equals(correctAnswer)) return;

        won = true;

        for (int i = 0; i < WORD_LENGTH * (WORD_LENGTH + 1); i++) {
            Label box = getBox(i);
            box.getStyleClass().add("correct");
        }
        
        scheduler.schedule(() -> winDialog.setVisible(true), 2, TimeUnit.SECONDS);
    }

    public void initializeJumble() {
        initializing = true;
        winDialog.setVisible(false);

        selected = 0;
        wordList = Jumble.getWordList(DIFFICULTY);

        for (int i = 0; i < WORD_LENGTH * (WORD_LENGTH + 1); i++) {
            getBox(i).getStyleClass().remove("selected");
            getBox(i).getStyleClass().remove("correct");
            getBox(i).setText("");
            
            if (i / WORD_LENGTH > WORD_LENGTH - 1) continue;

            GridPane grid = (GridPane)(Node)jumbleContainer.getChildren().get((i / WORD_LENGTH) * 2 + 1);
            Circle circle = (Circle)(Node)grid.getChildren().get((i % WORD_LENGTH) * 2 + 1);
            circle.setVisible(false);
        }

        getBox(selected).getStyleClass().add("selected");

        String[] jumbles = Jumble.generateJumble(0);
        
        Random rand = new Random();

        String selectedJumble = jumbles[rand.nextInt(jumbles.length)];
        String[] parts = selectedJumble.split(":");
        String clue = parts[0];
        correctAnswer = parts[1];
        String answer;

        do
            answer = Jumble.scramble(parts[1]);
        while (answer.equals(parts[1]));

        clueLabel.setText(clue);

        String[] letters = new String[answer.length()];
        List<String> selectedWords = new ArrayList<>();
        int[] lettersContained = new int[answer.length()];

        for (int i = 0; i < answer.length(); i++) {
            letters[i] = Character.toString(answer.charAt(i));
            String randomWord;
            
            do
                randomWord = Jumble.randomWord(DIFFICULTY);
            while (randomWord.equals(answer) || !randomWord.contains(letters[i]) || selectedWords.contains(randomWord));

            selectedWords.add(randomWord);
            lettersContained[i] = randomWord.indexOf(letters[i]);

            Label label = (Label)(Node)jumbleContainer.getChildren().get(i * 2);
            GridPane grid = (GridPane)(Node)jumbleContainer.getChildren().get(i * 2 + 1);
            String scambledWord;

            do
                scambledWord = Jumble.scramble(randomWord);
            while (scambledWord.equals(randomWord));

            label.setText(scambledWord.toUpperCase());
            
            Circle circle = (Circle)(Node)grid.getChildren().get(lettersContained[i] * 2 + 1);
            circle.setVisible(true);
        }

        initializing = false;
        won = false;
        System.out.println(parts[1]);
    }

    public void insertLetter(String key) {
        if (initializing || won) return;

        if (key.equals("BACK_SPACE")) { // backspace pressed
            GridPane grid = (GridPane)(Parent)getBox(selected).getParent();

            for (int i = 0; i < (grid.getChildren().size() / 2); i++) {
                Label box = (Label)(Node)grid.getChildren().get(i * 2);
                box.getStyleClass().remove("invalid");
            }

            if (getBox(selected).getText().equals("") && selected != 0) {
                getBox(selected).getStyleClass().remove("selected");
                    
                selected -= 1;
                getBox(selected).getStyleClass().add("selected");
                getBox(selected).setText("");

                grid = (GridPane)(Parent)getBox(selected).getParent();

                for (int i = 0; i < (grid.getChildren().size() / 2); i++) {
                    Label box = (Label)(Node)grid.getChildren().get(i * 2);
                    box.getStyleClass().remove("invalid");
                }

                return;
            }
            
            getBox(selected).setText("");
            checkClue();
        } else if (key.length() == 1) { // letter key pressed
            getBox(selected).setText(key.toUpperCase());

            GridPane grid = (GridPane)(Parent)getBox(selected).getParent();
            String scrambledWord;

            if (selected / WORD_LENGTH <= WORD_LENGTH - 1)
                scrambledWord = ((Label)(Node)jumbleContainer.getChildren().get((selected / WORD_LENGTH) * 2)).getText().toLowerCase();
            else
                scrambledWord = getCircled();

            String fullWord = "";

            for (int i = 0; i < (grid.getChildren().size() / 2); i++) {
                Label box = (Label)(Node)grid.getChildren().get(i * 2);
                fullWord += box.getText().toLowerCase();
            }

            Set<String> set = new HashSet<>(Arrays.asList(wordList));

            if (grid != clueGrid || (grid == clueGrid && allFilled())) {
                for (int i = 0; i < (grid.getChildren().size() / 2); i++) {
                    Label box = (Label)(Node)grid.getChildren().get(i * 2);

                    if ((Jumble.areAnagrams(scrambledWord, fullWord) && set.contains(fullWord)) || fullWord.length() != WORD_LENGTH)
                        box.getStyleClass().remove("invalid");
                    else
                        if(!box.getStyleClass().contains("invalid")) box.getStyleClass().add("invalid");
                }
            }
                
            checkClue();

            if (allFilled()) checkGame();

            if (selected == WORD_LENGTH * (WORD_LENGTH + 1) - 1) return;

            getBox(selected).getStyleClass().remove("selected");
                    
            selected += 1;
            getBox(selected).getStyleClass().add("selected");
        } else { // arrow key pressed
            switch (key) {
                case "UP":
                    if (selected / WORD_LENGTH == 0) return;
                    getBox(selected).getStyleClass().remove("selected");

                    selected -= WORD_LENGTH;
                    getBox(selected).getStyleClass().add("selected");

                    break;
                case "DOWN":
                    if (selected / WORD_LENGTH == WORD_LENGTH) return;
                    getBox(selected).getStyleClass().remove("selected");

                    selected += WORD_LENGTH;
                    getBox(selected).getStyleClass().add("selected");

                    break;
                case "LEFT":
                    if (selected == 0) return;
                    getBox(selected).getStyleClass().remove("selected");
                    
                    selected -= 1;
                    getBox(selected).getStyleClass().add("selected");

                    break;
                case "RIGHT":
                    if (selected == WORD_LENGTH * (WORD_LENGTH + 1) - 1) return;
                    getBox(selected).getStyleClass().remove("selected");
                    
                    selected += 1;
                    getBox(selected).getStyleClass().add("selected");

                    break;
            }
        }
    }
}
