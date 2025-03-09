package com.example.module3_playingcards;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CardGame extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CardGame.class.getResource("CardGame.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 707, 455);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("CardGame.css")).toExternalForm());
        stage.setTitle("Card Game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}