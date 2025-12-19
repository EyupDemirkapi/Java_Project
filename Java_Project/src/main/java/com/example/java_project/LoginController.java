package com.example.java_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            int id = Integer.parseInt(idField.getText());
            String pass = passwordField.getText();

            User loggedInUser = null;
            // DataStore içindeki kullanıcıları kontrol et
            for (User u : DataStore.getUsers()) {
                if (u.getID() == id && u.getPassword().equals(pass)) {
                    loggedInUser = u;
                    break;
                }
            }

            if (loggedInUser != null) {
                switchToMainScene(event, loggedInUser);
            } else {
                System.out.println("Hata: ID veya Şifre yanlış!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Hata: ID rakam olmalıdır!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchToMainScene(ActionEvent event, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();

        // Veriyi diğer controller'a gönder
        MainController mainController = loader.getController();
        mainController.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}