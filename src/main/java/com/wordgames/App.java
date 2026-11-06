package com.wordgames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("UI.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 640, 480);
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Word Games");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}