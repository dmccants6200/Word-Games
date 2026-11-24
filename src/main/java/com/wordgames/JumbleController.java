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
import javafx.util.Duration;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class JumbleController {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @FXML
    private VBox easyJumble, mediumJumble, hardJumble;

    @FXML
    private Label clueLabel;

    @FXML
    private GridPane easyClue, mediumClue, hardClue;

    @FXML
    private DialogPane winDialog;

    @FXML
    private ScrollPane help;

    @FXML
    private StackPane gamePane;

    @FXML
    private BorderPane cluePane;

    @FXML
    private Button helpButton, viewResults;

    @FXML
    void exitToMenu(ActionEvent event) throws IOException {
        if (initializing) return;
        FXMLLoader mainUI = new FXMLLoader(App.class.getResource("UI.fxml"));
        double x = ((Node)event.getSource()).getScene().getWidth(), y = ((Node)event.getSource()).getScene().getHeight();

        Scene scene = new Scene(mainUI.load(), x, y);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Boolean fullscreen = stage.isFullScreen();
        stage.setTitle("Word Games");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(fullscreen);
        stage.show();
    }

    @FXML
    void openHelp(ActionEvent event) {
        help.setVisible(!help.isVisible());
    }

    GaussianBlur blur = new GaussianBlur(10);
    
    @FXML
    void showResults(ActionEvent event) {
        viewResults.setVisible(false);
        winDialog.setVisible(true);
        gamePane.setEffect(blur);
        cluePane.setEffect(blur);
    }

    private Boolean initializing = false, won = false;
    private int selected, DIFFICULTY, WORD_LENGTH, columnCount, rowCount;
    String[] wordList = null;
    String correctAnswer;

    GridPane currentClue;
    VBox currentJumble;
    Rotate rotateX, rotateY;

    private Timeline rotationAnimation;
    
    public void setDifficulty(int difficulty) {
        DIFFICULTY = difficulty;
        WORD_LENGTH = difficulty + 3;

        easyJumble.setVisible(difficulty == 0);
        easyClue.setVisible(difficulty == 0);
        mediumJumble.setVisible(difficulty == 1);
        mediumClue.setVisible(difficulty == 1);
        hardJumble.setVisible(difficulty == 2);
        hardClue.setVisible(difficulty == 2);

        currentJumble = difficulty == 0 ? easyJumble : (difficulty == 1 ? mediumJumble : hardJumble);
        currentClue = difficulty == 0 ? easyClue : (difficulty == 1 ? mediumClue : hardClue);

        rotateX = new Rotate(0, currentJumble.getWidth() / 2, currentJumble.getHeight() / 2, 0, Rotate.Y_AXIS);
        rotateY = new Rotate(0, currentJumble.getWidth() / 2, currentJumble.getHeight() / 2, 0, Rotate.X_AXIS);

        currentJumble.layoutBoundsProperty().addListener((obs, oldBounds, bounds) -> {
            double centerX = bounds.getMinX() + bounds.getWidth() / 2;
            double centerY = bounds.getMinY() + bounds.getHeight() / 2;

            rotateX.setPivotX(centerX);
            rotateX.setPivotY(centerY);

            rotateY.setPivotX(centerX);
            rotateY.setPivotY(centerY);
        });

        currentJumble.getTransforms().addAll(rotateX, rotateY);
    }
    
    public void setSelected(int index) {
        selected = index;

        if (rotationAnimation != null)
            rotationAnimation.stop();

        if (index / WORD_LENGTH <= WORD_LENGTH - 1) {
            double maxTilt = 5;

            double columnPercent = (columnCount - 2.0 * (selected % columnCount + 1)) / (columnCount - 1);
            double rowPercent = (2.0 * (selected / columnCount + 1) - rowCount - 1) / (rowCount - 1);

            double targetX = maxTilt * columnPercent;
            double targetY = maxTilt * rowPercent;

            // Stop any previous animation

            rotationAnimation = new Timeline(
                new KeyFrame(
                    Duration.millis(150),
                    new KeyValue(rotateX.angleProperty(), targetX, Interpolator.EASE_BOTH),
                    new KeyValue(rotateY.angleProperty(), targetY, Interpolator.EASE_BOTH)
                )
            );
        } else {
            rotationAnimation = new Timeline(
                new KeyFrame(
                    Duration.millis(150),
                    new KeyValue(rotateX.angleProperty(), 0, Interpolator.EASE_BOTH),
                    new KeyValue(rotateY.angleProperty(), 0, Interpolator.EASE_BOTH)
                )
            );
        }

        rotationAnimation.play();
    }

    @FXML
    public void initialize() {
        Button nextButton = (Button) winDialog.lookupButton(ButtonType.NEXT),
        cancelButton = (Button)winDialog.lookupButton(ButtonType.CLOSE);
        
        nextButton.setText("Next Round");
        nextButton.setOnAction(event -> initializeJumble());
        cancelButton.setText("View Game");
        cancelButton.setOnAction(event -> {
            winDialog.setVisible(false);
            viewResults.setVisible(true);
            gamePane.setEffect(null);
            cluePane.setEffect(null);
        });
    }

    private Label getBox(int index) {
        if (index / WORD_LENGTH <= WORD_LENGTH - 1) {
            GridPane grid = (GridPane)(Node)currentJumble.getChildren().get((index / WORD_LENGTH) * 2 + 1);
            
            return (Label)(Node)grid.getChildren().get((index % WORD_LENGTH) * 2);
        } else
            return (Label)(Node)currentClue.getChildren().get((index % WORD_LENGTH) * 2);
    }

    private String getCircled() {
        String circledLetters = "";

        for (int i = 0; i < WORD_LENGTH * WORD_LENGTH; i++) {
            GridPane grid = (GridPane)(Node)currentJumble.getChildren().get((i / WORD_LENGTH) * 2 + 1);
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

        for (int i = 0; i < (currentClue.getChildren().size() / 2); i++) {
            Label box = (Label)(Node)currentClue.getChildren().get(i * 2);
            fullWord += box.getText().toLowerCase();
        }

        if ((!(set.contains(fullWord) && Jumble.areAnagrams(scrambledWord, fullWord)) || invalid) && allFilled() && fullWord.length() == WORD_LENGTH) {
            for (int i = 0; i < (currentClue.getChildren().size() / 2); i++) {
                Label box = (Label)(Node)currentClue.getChildren().get(i * 2);
                if(!box.getStyleClass().contains("invalid")) box.getStyleClass().add("invalid");
            }

            return;
        }

        for (int i = 0; i < (currentClue.getChildren().size() / 2); i++) {
            Label box = (Label)(Node)currentClue.getChildren().get(i * 2);
            box.getStyleClass().remove("invalid");
        }
    }

    private void checkGame() {
        for (int i = 0; i < WORD_LENGTH * (WORD_LENGTH + 1); i++)
            if (getBox(i).getStyleClass().contains("invalid")) return;
        
        String typedClue = "";

        for (int i = 0; i < (currentClue.getChildren().size() / 2); i++) {
            Label box = (Label)(Node)currentClue.getChildren().get(i * 2);
            typedClue += box.getText().toLowerCase();
        }

        if (!typedClue.equals(correctAnswer)) return;

        won = true;

        for (int i = 0; i < WORD_LENGTH * (WORD_LENGTH + 1); i++) {
            Label box = getBox(i);
            box.getStyleClass().add("correct");
        }
        
        if (rotationAnimation != null)
            rotationAnimation.stop();

        rotationAnimation = new Timeline(
            new KeyFrame(
                Duration.millis(150),
                new KeyValue(rotateX.angleProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(rotateY.angleProperty(), 0, Interpolator.EASE_BOTH)
            )
        );

        rotationAnimation.play();

        scheduler.schedule(() -> {
            winDialog.setVisible(true);
            gamePane.setEffect(blur);
            cluePane.setEffect(blur);
            helpButton.setVisible(false);
            help.setVisible(false);
        }, 2, TimeUnit.SECONDS);
    }

    public void initializeJumble() {
        initializing = true;
        winDialog.setVisible(false);
        helpButton.setVisible(true);
        gamePane.setEffect(null);
        cluePane.setEffect(null);

        columnCount = WORD_LENGTH;
        rowCount = WORD_LENGTH;

        setSelected(0);
        wordList = WordLadder.getWordList(DIFFICULTY);

        for (int i = 0; i < WORD_LENGTH * (WORD_LENGTH + 1); i++) {
            getBox(i).getStyleClass().remove("selected");
            getBox(i).getStyleClass().remove("correct");
            getBox(i).setText("");
            
            if (i / WORD_LENGTH > WORD_LENGTH - 1) continue;

            GridPane grid = (GridPane)(Node)currentJumble.getChildren().get((i / WORD_LENGTH) * 2 + 1);
            Circle circle = (Circle)(Node)grid.getChildren().get((i % WORD_LENGTH) * 2 + 1);
            circle.setVisible(false);
        }

        getBox(selected).getStyleClass().add("selected");

        String[] jumbles = Jumble.generateJumble(DIFFICULTY);
        
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
            while (randomWord.equals(correctAnswer) || !randomWord.contains(letters[i]) || selectedWords.contains(randomWord));

            selectedWords.add(randomWord);
            lettersContained[i] = randomWord.indexOf(letters[i]);

            Label label = (Label)(Node)currentJumble.getChildren().get(i * 2);
            GridPane grid = (GridPane)(Node)currentJumble.getChildren().get(i * 2 + 1);
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
        if (initializing || won || help.isVisible()) return;

        if (key.equals("BACK_SPACE")) { // backspace pressed
            GridPane grid = (GridPane)(Parent)getBox(selected).getParent();

            for (int i = 0; i < (grid.getChildren().size() / 2); i++) {
                Label box = (Label)(Node)grid.getChildren().get(i * 2);
                box.getStyleClass().remove("invalid");
            }

            if (getBox(selected).getText().equals("") && selected != 0) {
                getBox(selected).getStyleClass().remove("selected");
                    
                setSelected(selected - 1);
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
                scrambledWord = ((Label)(Node)currentJumble.getChildren().get((selected / WORD_LENGTH) * 2)).getText().toLowerCase();
            else
                scrambledWord = getCircled();

            String fullWord = "";

            for (int i = 0; i < (grid.getChildren().size() / 2); i++) {
                Label box = (Label)(Node)grid.getChildren().get(i * 2);
                fullWord += box.getText().toLowerCase();
            }

            Set<String> set = new HashSet<>(Arrays.asList(wordList));

            if (grid != currentClue || (grid == currentClue && allFilled())) {
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
                    
            setSelected(selected + 1);
            getBox(selected).getStyleClass().add("selected");
        } else { // arrow key pressed
            switch (key) {
                case "UP":
                    if (selected / WORD_LENGTH == 0) return;
                    getBox(selected).getStyleClass().remove("selected");

                    setSelected(selected - WORD_LENGTH);
                    getBox(selected).getStyleClass().add("selected");

                    break;
                case "DOWN":
                    if (selected / WORD_LENGTH == WORD_LENGTH) return;
                    getBox(selected).getStyleClass().remove("selected");

                    setSelected(selected + WORD_LENGTH);
                    getBox(selected).getStyleClass().add("selected");

                    break;
                case "LEFT":
                    if (selected == 0) return;
                    getBox(selected).getStyleClass().remove("selected");
                    
                    setSelected(selected - 1);
                    getBox(selected).getStyleClass().add("selected");

                    break;
                case "RIGHT":
                    if (selected == WORD_LENGTH * (WORD_LENGTH + 1) - 1) return;
                    getBox(selected).getStyleClass().remove("selected");
                    
                    setSelected(selected + 1);
                    getBox(selected).getStyleClass().add("selected");

                    break;
            }
        }
    }
}
