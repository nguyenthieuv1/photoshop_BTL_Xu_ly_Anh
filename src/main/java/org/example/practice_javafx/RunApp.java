package org.example.practice_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RunApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
        primaryStage.setTitle("Simple Photoshop App");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
