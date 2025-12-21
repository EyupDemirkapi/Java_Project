package com.example.java_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private VBox loginPane;
    @FXML private VBox signupPane;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField newName;
    @FXML private TextField newEmail;
    @FXML private PasswordField newPass;
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
        // 1. Rol kontrolüne göre FXML dosyasını belirle
        String targetFXML = user.getRole().equals("Teacher") ? "mid2-view.fxml" : "mid1-view.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(targetFXML));
        Parent root = loader.load(); // ÖNCE yükle, sonra controller'a eriş

        // 2. Controller'a veriyi güvenli bir şekilde aktar
        if (user.getRole().equals("Teacher")) {
            Mid2Controller controller = loader.getController();
            controller.setUser(user); // Mid2Controller'da setUser metodun olmalı
        } else {
            Mid1Controller controller = loader.getController();
            controller.setUser(user); // Mid1Controller'da setUser metodun olmalı
        }

        // 3. Sahneyi değiştir ve tam ekran yap
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true); // Ekranı kapla
        stage.show();
    }
    //devam


    @FXML
    public void initialize() {
        // Rolleri doldur
        if(roleCombo != null) {
            roleCombo.getItems().addAll("Student", "Teacher", "Editor");
        }
    }

    @FXML
    private void showSignUp() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        signupPane.setVisible(true);
        signupPane.setManaged(true);
    }

    @FXML
    private void showLogin() {
        signupPane.setVisible(false);
        signupPane.setManaged(false);
        loginPane.setVisible(true);
        loginPane.setManaged(true);
    }

    @FXML
    private void processSignUp() {
        try {
            String name = newName.getText();
            String email = newEmail.getText();
            String password = newPass.getText();
            String role = roleCombo.getValue(); // ComboBox'tan seçilen rol

            if (name.isEmpty() || role == null) {
                System.out.println("Hata: Lütfen tüm alanları doldurun!");
                return;
            }

            // Benzersiz bir ID oluştur (Basitlik için random integer)
            int newId = (int) (Math.random() * 9000) + 1000;

            User newUser;
            if (role.equals("Teacher")) {
                newUser = new Teacher(name, "", newId, password, "Genel", "Uzman");
            } else {
                newUser = new Student(name, "", newId, password, "Genel", "1. Sınıf");
            }

            // DataStore'a ekle ve kaydet
            DataStore.addUser(newUser);

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Kayıt Başarılı");
            alert.setHeaderText("Giriş Bilgileriniz");
            alert.setContentText("Kayıt tamamlandı! Giriş ID'niz: " + newId + "\nLütfen bu ID'yi not alın.");
            alert.showAndWait();
            showLogin(); // Kullanıcıyı tekrar giriş ekranına gönder

        } catch (Exception e) {
            System.out.println("Kayıt sırasında bir hata oluştu.");
        }
    }
}