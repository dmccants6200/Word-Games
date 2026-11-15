package com.wordgames;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class WordLadderController {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @FXML
    private GridPane wordGrid;

    @FXML
    private DialogPane winDialog;

    @FXML
    public void initialize() {
        Button nextButton = (Button) winDialog.lookupButton(ButtonType.NEXT);
        
        //nextButton.addEventFilter(ActionEvent.ACTION, event -> {
        nextButton.setOnAction(event -> initializeWordLadder(/*wordGrid.getColumnCount() - 3*/));
    }

    String[] wordList = null;

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

        for (int i = 0; i < wordGrid.getRowCount() ; i++) {
            String word = "";

            for(int j = 0; j < wordGrid.getColumnCount(); j++)
                word = word + getBox(j + i * wordGrid.getColumnCount()).getText().toLowerCase();

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

        for (int i = 0; i < wordGrid.getChildren().size(); i++) {
            getBox(i).setText("");
            getBox(i).getStyleClass().remove("correct");
            getBox(i).getStyleClass().remove("selected");
        }

        String selectedLadder = ladders[rand.nextInt(ladders.length)];
         String[] parts = selectedLadder.split(":");

        System.out.println(selectedLadder);

        selected = DIFFICULTY + 3;
        getBox(selected).getStyleClass().add("selected");

        for (int i = 0; i < DIFFICULTY + 3; i++) {
            Label topBox = (Label) wordGrid.getChildren().get(i);
            topBox.setText(Character.toString(parts[0].charAt(i)).toUpperCase());

            Label bottomBox = (Label) wordGrid.getChildren().get(i + (int)Math.pow(DIFFICULTY + 3, 2)); // 9  16 25
            bottomBox.setText(Character.toString(parts[3 + DIFFICULTY].charAt(i)).toUpperCase());
        }

        initializing = false;
        won = false;
    }

    public void insertLetter(String key) {
        if (initializing || won) return;

        if (!key.equals("BACK_SPACE")) {
            Label selectedBox = getBox(selected);

            if (selected == 8 && selectedBox.getText() != "") return;

            if (getBox(selected - 1).getStyleClass().contains("invalid")
             || getBox(selected - 1).getStyleClass().contains("not-one-off")) return;

            if (selected == 5 || selected == 8 && selectedBox.getText() == "") { // typed full word
                Set<String> set = new HashSet<>(Arrays.asList(wordList));

                String fullWord = (getBox(selected - 2).getText()
                        + getBox(selected - 1).getText()
                        + key).toLowerCase();

                boolean found = set.contains(fullWord);

                if (found) {
                    Set<String> pairSet = new HashSet<>(Arrays.asList(WordLadder.getPairs(DIFFICULTY, true)));

                    String prevWord = (getBox(selected - 5).getText()
                            + getBox(selected - 4).getText()
                            + getBox(selected - 3).getText()).toLowerCase();

                    if (fullWord.equals(prevWord)) {
                        for (int i = selected; i > selected - 3; i--)
                            getBox(i).getStyleClass().add("invalid");
                    }

                    for (int i = selected; i > selected - 3; i--) {
                        //getBox(i).getStyleClass().remove("invalid");
                        if (!pairSet.contains(prevWord + ":" + fullWord) && !fullWord.equals(prevWord)) getBox(i).getStyleClass().add("not-one-off");
                    }

                    if (selected == 8) {
                        String nextWord = (getBox(selected + 1).getText()
                            + getBox(selected + 2).getText()
                            + getBox(selected + 3).getText()).toLowerCase();

                        if (fullWord.equals(nextWord)) {
                            for (int i = selected; i > selected - 3; i--)
                                getBox(i).getStyleClass().add("invalid");
                        }

                        for (int i = selected; i > selected - 3; i--) {
                            //getBox(i).getStyleClass().remove("invalid");
                            if (!pairSet.contains(fullWord + ":" + nextWord) && !fullWord.equals(nextWord)) getBox(i).getStyleClass().add("not-one-off");
                        }
                    }

                } else {
                    for (int i = selected; i > selected - 3; i--)
                        getBox(i).getStyleClass().add("invalid");
                }
            }

            if (selected == 8 && selectedBox.getText() == "") {
                selectedBox.setText(key);

                Set<String> pairSet = new HashSet<>(Arrays.asList(WordLadder.getPairs(DIFFICULTY, true)));
                String[] ladder = getFullLadder();

                if (pairSet.contains(ladder[0] + ":" + ladder[1])
                 && pairSet.contains(ladder[1] + ":" + ladder[2])
                 && pairSet.contains(ladder[2] + ":" + ladder[3])) { // change with diff
                    won = true;
                    for (int i = 0; i < wordGrid.getChildren().size(); i++)
                        getBox(i).getStyleClass().add("correct");
                    
                    scheduler.schedule(() -> winDialog.setVisible(true), 2, TimeUnit.SECONDS);
                }

                return;
            }

            selectedBox.setText(key);
            selectedBox.getStyleClass().remove("selected");
            getBox(selected + 1).getStyleClass().add("selected");

            selected += 1;
        } else {
            if (selected == 3) return;

            if (selected == 6) {
                for (int i = selected - 1; i > selected - 4; i--) {
                    getBox(i).getStyleClass().remove("invalid");
                    getBox(i).getStyleClass().remove("not-one-off");
                }
            }

            Label selectedBox = getBox(selected);

            if (selected == 8 && selectedBox.getText() != "") {
                for (int i = 0; i < wordGrid.getChildren().size(); i++)
                    getBox(i).getStyleClass().remove("correct");
                
                selectedBox.setText("");

                for (int i = selected; i > selected - 3; i--) {
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