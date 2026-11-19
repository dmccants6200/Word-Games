package com.wordgames;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WordLadderController {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @FXML
    private GridPane wordGrid;

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

    @FXML
    public void initialize() {
        Button nextButton = (Button) winDialog.lookupButton(ButtonType.NEXT);
        
        nextButton.setOnAction(event -> initializeWordLadder());
    }

    String[] wordList = null;

    int columnCount;
    int rowCount;
    int selected;
    Boolean initializing = false;
    Boolean won = false;
    private int DIFFICULTY;

    public void setDifficulty(int difficulty) {
        DIFFICULTY = difficulty;
    }

    public int getDifficulty() {
        return DIFFICULTY;
    }

    private Label getBox(int position) {
        return (Label) wordGrid.getChildren().get(position);
    }

    private String[] getFullLadder() {
        List<String> ladder = new ArrayList<>();

        for (int i = 0; i < rowCount ; i++) {
            String word = "";

            for(int j = 0; j < columnCount; j++)
                word = word + getBox(j + i * columnCount).getText().toLowerCase();

            ladder.add(word);
        }

        return ladder.toArray(new String[0]);
    }

    public void initializeWordLadder() {
        initializing = true;
        winDialog.setVisible(false);
        final String[] ladders = WordLadder.generateLadder(DIFFICULTY);
        wordList = WordLadder.getWordList(DIFFICULTY);
        Random rand = new Random();

        columnCount = wordGrid.getColumnCount();
        rowCount = wordGrid.getRowCount();

        for (Node box : wordGrid.getChildren()) {
            ((Label)box).setText("");
            ((Label)box).getStyleClass().remove("correct");
            ((Label)box).getStyleClass().remove("selected");
        }

        String selectedLadder = ladders[rand.nextInt(ladders.length)];
        String[] parts = selectedLadder.split(":");

        System.out.println(selectedLadder);

        selected = DIFFICULTY + 3;
        getBox(selected).getStyleClass().add("selected");

        for (int i = 0; i < DIFFICULTY + 3; i++) {
            Label topBox = getBox(i);
            topBox.setText(Character.toString(parts[0].charAt(i)).toUpperCase());

            Label bottomBox = getBox(i + (int)Math.pow(DIFFICULTY + 3, 2)); // 9  16 25
            bottomBox.setText(Character.toString(parts[3 + DIFFICULTY].charAt(i)).toUpperCase());
        }

        initializing = false;
        won = false;
    }

    public void insertLetter(String key) {
        if (initializing || won) return;

        Boolean endOfWord = (selected + 1) % columnCount == 0;
        Boolean endOfLadder = endOfWord && selected == (columnCount * rowCount - columnCount - 1);

        if (!key.equals("BACK_SPACE")) {
            Label selectedBox = getBox(selected);

            if (endOfLadder && selectedBox.getText() != "") return;

            if (getBox(selected - 1).getStyleClass().contains("invalid")
             || getBox(selected - 1).getStyleClass().contains("not-one-off")) return;

            if (endOfWord) { // typed full word
                Set<String> set = new HashSet<>(Arrays.asList(wordList));
                String fullWord = "";

                for (int i = columnCount - 1; i > 0; i--)
                    fullWord += getBox(selected - i).getText().toLowerCase();
                
                fullWord += key.toLowerCase();

                if (set.contains(fullWord)) {
                    Set<String> pairSet = new HashSet<>(Arrays.asList(WordLadder.getPairs(DIFFICULTY, true)));
                    String prevWord = "";
                    String nextWord = "";

                    for (int i = 1; i <= columnCount; i++) {
                        prevWord += getBox(selected - (columnCount * 2 - i)).getText().toLowerCase();
                        nextWord += getBox(selected + i).getText().toLowerCase();
                    }
                    
                    if (fullWord.equals(prevWord) || fullWord.equals(nextWord))
                        for (int i = selected; i > selected - 3; i--)
                            getBox(i).getStyleClass().add("invalid");
                    else if (!pairSet.contains(prevWord + ":" + fullWord) || (!nextWord.equals("") && !pairSet.contains(fullWord + ":" + nextWord)))
                        for (int i = selected; i > selected - 3; i--)
                            getBox(i).getStyleClass().add("not-one-off");
                } else
                    for (int i = selected; i > selected - 3; i--)
                        getBox(i).getStyleClass().add("invalid");
            }

            if (endOfLadder) {
                selectedBox.setText(key);

                Set<String> pairSet = new HashSet<>(Arrays.asList(WordLadder.getPairs(DIFFICULTY, true)));
                String[] ladder = getFullLadder();

                for (int i = 0; i < ladder.length - 1; i++)
                    if (!pairSet.contains(ladder[i] + ":" + ladder[i + 1])) return;

                won = true;

                for (Node box : wordGrid.getChildren())
                    box.getStyleClass().add("correct");

                scheduler.schedule(() -> winDialog.setVisible(true), 2, TimeUnit.SECONDS);

                return;
            }

            selectedBox.setText(key);
            selectedBox.getStyleClass().remove("selected");
            getBox(selected + 1).getStyleClass().add("selected");

            selected += 1;
        } else {
            if (selected == columnCount) return;

            if (selected % columnCount == 0)
                for (int i = selected; i > selected - columnCount; i--) {
                    getBox(i - 1).getStyleClass().remove("invalid");
                    getBox(i - 1).getStyleClass().remove("not-one-off");
                }

            Label selectedBox = getBox(selected);

            if (endOfLadder && selectedBox.getText() != "") {
                selectedBox.setText("");

                for (int i = selected; i > selected - columnCount; i--) {
                    getBox(i).getStyleClass().remove("invalid");
                    getBox(i).getStyleClass().remove("not-one-off");
                }

                return;
            }

            Label prevBox = getBox(selected - 1);

            prevBox.setText("");
            selectedBox.getStyleClass().remove("selected");
            prevBox.getStyleClass().add("selected");

            selected -= 1;
        }
    }
}