package com.example.java_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class LoginController {
    @FXML private AnchorPane rootAnchorPane;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private VBox loginPane;
    @FXML private VBox signupPane;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField newName;
    @FXML private TextField newEmail;
    @FXML private PasswordField newPass;

    @FXML
    public void initialize() {
        // Rolleri doldur ve stil ver
        if(roleCombo != null) {
            roleCombo.getItems().setAll("Student", "Teacher", "Editor");
            roleCombo.setStyle("-fx-background-color: #cfd8dc; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 5;");
        }

        // --- GİZLİ KISAYOL: CTRL + ALT + R ---
        Platform.runLater(() -> {
            if (rootAnchorPane != null && rootAnchorPane.getScene() != null) {
                rootAnchorPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.isControlDown() && event.isAltDown() && event.getCode() == KeyCode.R) {
                        handleResetSystem();
                        event.consume();
                    }
                });
            }
        });
    }

    private void handleResetSystem() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "DİKKAT! Tüm veriler silinecek. Emin misiniz?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Gizli Sıfırlama Menüsü");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                DataStore.clearAllData();
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Tüm sistem verileri sıfırlandı.");
            }
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            if(idField.getText().isEmpty()) return;
            int id = Integer.parseInt(idField.getText());
            String pass = passwordField.getText();

            User loggedInUser = null;
            for (User u : DataStore.getUsers()) {
                if (u.getID() == id && u.getPassword().equals(pass)) {
                    loggedInUser = u;
                    break;
                }
            }

            if (loggedInUser != null) {
                switchToMainScene(event, loggedInUser);
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "ID veya Şifre yanlış!");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Hata", "ID rakam olmalıdır!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchToMainScene(ActionEvent event, User user) throws IOException {
        String targetFXML = (user instanceof Teacher) ? "mid2-view.fxml" : "mid1-view.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(targetFXML));
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof Mid2Controller) {
            ((Mid2Controller) controller).setUser(user);
        } else if (controller instanceof Mid1Controller) {
            ((Mid1Controller) controller).setUser(user);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    @FXML
    private void processSignUp() {
        try {
            String name = newName.getText();
            String email = newEmail.getText();
            String password = newPass.getText();
            String role = roleCombo.getValue();

            if (name.isEmpty() || role == null || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Eksik", "Lütfen tüm alanları doldurun!");
                return;
            }

            int newId = (int) (Math.random() * 9000) + 1000;
            User newUser;

            if ("Teacher".equals(role)) {
                newUser = new Teacher(name, email, newId, password, "Genel", "Uzman");
            } else if ("Editor".equals(role)) {
                newUser = new Editor(name, email, newId, password, "Genel", "Moderasyon");
            } else {
                newUser = new Student(name, email, newId, password, "Genel", "1. Sınıf");
            }

            DataStore.addUser(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "ID'niz: " + newId);
            showLogin();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void showSignUp() { loginPane.setVisible(false); loginPane.setManaged(false); signupPane.setVisible(true); signupPane.setManaged(true); }
    @FXML private void showLogin() { signupPane.setVisible(false); signupPane.setManaged(false); loginPane.setVisible(true); loginPane.setManaged(true); }

    @FXML
    private void handleForgotPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Şifre Sıfırlama");
        dialog.setHeaderText("E-posta adresinizi girin:");
        dialog.showAndWait().ifPresent(email -> {
            User foundUser = DataStore.getUsers().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst().orElse(null);
            if (foundUser != null) {
                String tempPass = "Temp" + (int)(Math.random() * 9000 + 1000);
                foundUser.setPassword(tempPass);
                DataStore.saveAll();
                new Thread(() -> MailService.sendResetMail(email, tempPass)).start();
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Yeni şifreniz gönderildi.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "E-posta bulunamadı!");
            }
        });
    }

    @FXML
    private void showHelp() {
        showAlert(Alert.AlertType.INFORMATION, "Yardım", "Yöneticiler: omeroduncu@marun.edu.tr\nemrearslan24@marun.edu.tr\neyupdemirkapi@marun.edu.tr\nyagmurbozkurt@marun.edu.tr");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- MAİL SERVİSİ ---
    public static class MailService {
        public static void sendResetMail(String recipientEmail, String newPassword) {
            final String username = "senin_mailin@gmail.com";
            final String password = "uygulama_sifren";
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Şifre Sıfırlama");
                message.setText("Geçici şifreniz: " + newPassword);
                Transport.send(message);
            } catch (MessagingException e) { e.printStackTrace(); }
        }
    }
}