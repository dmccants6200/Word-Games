package com.wordgames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) throws IOException {
        FXMLLoader mainUI = new FXMLLoader(App.class.getResource("UI.fxml"));
        Scene scene = new Scene(mainUI.load(), 640, 480);
        
        //primaryStage.setFullScreen(true);
        primaryStage.setTitle("Word Games");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}