package com.example.java_project;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void onLogoutButtonClick(ActionEvent event) {
        try {
            // 1. hello-view.fxml dosyasını yükle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // 2. Mevcut Stage'i (pencereyi) al
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Yeni sahneye geçiş yap
            stage.setScene(scene);
            stage.setTitle("Giriş Ekranı");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

