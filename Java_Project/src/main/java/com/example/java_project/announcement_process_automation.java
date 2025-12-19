package com.example.java_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class announcement_process_automation extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Burayı login-view.fxml olarak değiştiriyoruz
        FXMLLoader fxmlLoader = new FXMLLoader(announcement_process_automation.class.getResource("login-view.fxml"));

        // 2. Sahne boyutunu giriş ekranına uygun yapalım (örneğin 400x300)
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        // 3. Başlığı giriş ekranına uygun şekilde güncelleyelim
        stage.setTitle("Duyuru Sistemi - Giriş");
        stage.setResizable(false); // Giriş ekranının boyutunun değiştirilmesini engellemek şık durur
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
