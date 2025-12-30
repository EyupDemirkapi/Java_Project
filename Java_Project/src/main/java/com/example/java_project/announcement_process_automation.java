package com.example.java_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class announcement_process_automation extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(announcement_process_automation.class.getResource("login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 425, 600);

        stage.setTitle("Duyuru Sistemi - Giriş");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }


}
