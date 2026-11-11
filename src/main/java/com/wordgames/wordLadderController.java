package com.wordgames;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class WordLadderController {
    @FXML
    private GridPane wordGrid;
    String[] wordList = null;

    int selected;
    Boolean initializing = false;

    private Label getBox(int position) {
        return (Label) wordGrid.getChildren().get(position);
    }

    private String[] getFullLadder() {
        List<String> ladder = new ArrayList<>();

        for (int i = 0; i < wordGrid.getRowCount() ; i++) {
            String word = "";

            for(int j = 0; j < wordGrid.getColumnCount(); j++) {
                word = word + getBox(j + i * wordGrid.getColumnCount()).getText().toLowerCase();
            }

            ladder.add(word);
        }

        return ladder.toArray(new String[0]);
    }

    public void initializeWordLadder(int difficulty) {
        initializing = true;
        final String[] ladders = WordLadder.generateLadder(difficulty);
        Random rand = new Random();

        for (int i = 0; i < wordGrid.getChildren().size(); i++) {
            getBox(i).setText("");
            getBox(i).getStyleClass().remove("correct");
            getBox(i).getStyleClass().remove("selected");
            getBox(i).getStyleClass().remove("valid");
        }

        switch (difficulty) {
            case 0:
                wordList = WordLadder.getWordList(difficulty);
                String selectedLadder = ladders[rand.nextInt(ladders.length)];
                String[] parts = selectedLadder.split(":");

                System.out.println(selectedLadder);

                selected = 3;
                getBox(selected).getStyleClass().add("selected");

                for (int i = 0; i < 3; i++) {
                    Label box = (Label) wordGrid.getChildren().get(i);

                    box.setText(Character.toString(parts[0].charAt(i)).toUpperCase());
                }

                for (int i = 0; i < 3; i++) {
                    Label box = (Label) wordGrid.getChildren().get(i + 9);

                    box.setText(Character.toString(parts[3].charAt(i)).toUpperCase());
                }

                break;
            case 1:

                break;
            case 2:

                break;
        }

        initializing = false;
    }

    public void insertLetter(String key) {
        if (initializing) return;
        if (!key.equals("BACK_SPACE")) {
            Label selectedBox = getBox(selected);

            if (getBox(selected - 1).getStyleClass().contains("invalid")
             || getBox(selected - 1).getStyleClass().contains("not-one-off"))
                return;

            if (selected == 5 || selected == 8 && selectedBox.getText() == "") { // typed full word
                Set<String> set = new HashSet<>(Arrays.asList(wordList));
                Set<String> pairSet = new HashSet<>(Arrays.asList(WordLadder.getPairs(wordList, true)));

                String fullWord = (getBox(selected - 2).getText()
                        + getBox(selected - 1).getText()
                        + key).toLowerCase();

                boolean found = set.contains(fullWord);

                if (found) {
                    String prevWord = (getBox(selected - 5).getText()
                            + getBox(selected - 4).getText()
                            + getBox(selected - 3).getText()).toLowerCase();

                    Boolean oneOff = pairSet.contains(prevWord + ":" + fullWord);

                    for (int i = selected; i > selected - 3; i--) {
                        getBox(i).getStyleClass().remove("invalid");
                        getBox(i).getStyleClass().add(oneOff ? "valid" : "not-one-off");
                        
                    }
                } else {
                    for (int i = selected; i > selected - 3; i--) {
                        getBox(i).getStyleClass().remove("valid");
                        getBox(i).getStyleClass().add("invalid");
                    }
                }

                //System.out.println((found ? "Contains " : "Does not contain ") + fullWord);
            }

            if (selected == 8 && selectedBox.getText() == "") {
                selectedBox.setText(key);

                Set<String> pairSet = new HashSet<>(Arrays.asList(WordLadder.getPairs(wordList, true)));
                String[] ladder = getFullLadder();

                if (pairSet.contains(ladder[0] + ":" + ladder[1])
                 && pairSet.contains(ladder[1] + ":" + ladder[2])
                 && pairSet.contains(ladder[2] + ":" + ladder[3])) { // change with diff
                    for (int i = 0; i < wordGrid.getChildren().size(); i++)
                        getBox(i).getStyleClass().add("correct");
                
                    initializeWordLadder(wordGrid.getColumnCount() - 3);
                }
                return;
            } else if (selected == 8 && selectedBox.getText() != "")
                return;

            selectedBox.setText(key);
            selectedBox.getStyleClass().remove("selected");
            getBox(selected + 1).getStyleClass().add("selected");

            selected += 1;
        } else {
            if (selected == 3)
                return;

            if (selected == 6) {
                for (int i = selected - 1; i > selected - 4; i--) {
                    getBox(i).getStyleClass().remove("valid");
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
                    getBox(i).getStyleClass().remove("valid");
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