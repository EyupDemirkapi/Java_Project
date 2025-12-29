package com.example.java_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
public class LoginController {
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private VBox loginPane;
    @FXML private VBox signupPane;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField newName;
    @FXML private TextField newEmail;
    @FXML private PasswordField newPass;


    public class MailService {
        public static void sendResetMail(String recipientEmail, String newPassword) {
            final String username = "senin_mailin@gmail.com";
            final String password = "uygulama_sifren"; // Gmail App Password

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
                message.setSubject("Şifre Sıfırlama Talebi");
                message.setText("Merhaba, yeni geçici şifreniz: " + newPassword + "\nLütfen giriş yaptıktan sonra şifrenizi değiştirin.");

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleForgotPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Şifre Sıfırlama");
        dialog.setHeaderText("Kayıtlı E-posta adresinizi girin:");
        dialog.setContentText("Email:");

        dialog.showAndWait().ifPresent(email -> {
            // 1. Sistemde bu maile sahip kullanıcıyı bul
            User foundUser = DataStore.getUsers().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst().orElse(null);

            if (foundUser != null) {
                // 2. Yeni bir geçici şifre oluştur
                String tempPass = "Temp" + (int)(Math.random() * 9000 + 1000);
                foundUser.setPassword(tempPass);
                DataStore.saveAll();

                // 3. Mail gönder (Ayrı bir thread'de yapılması donmayı engeller)
                new Thread(() -> {
                    MailService.sendResetMail(email, tempPass);
                }).start();

                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Yeni şifreniz mail adresinize gönderildi.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Bu e-posta adresi sistemde kayıtlı değil!");
            }
        });
    }
    @FXML
    public void initialize() {
      
        if(roleCombo != null) {
            roleCombo.getItems().setAll("Student", "Teacher", "Editor");

            // --- RENK GÜNCELLEMESİ ---
            // Arka planı daha açık bir gri (#b0bec5) veya beyazımsı yapalım
            roleCombo.setStyle(
                    "-fx-background-color: #cfd8dc; " + // Açık gümüş/mavi gri tonu
                            "-fx-text-fill: black; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 5;"
            );
        }
        if(roleCombo != null) {
            roleCombo.getItems().setAll("Student", "Teacher", "Editor");
        }
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

    @FXML
    private void handleResetSystem() {
        // Sistemi sıfırlama butonu için onay kutusu
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Tüm veriler silinecek. Emin misiniz?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Sistemi Sıfırla");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                DataStore.clearAllData();
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Sistem sıfırlandı. Lütfen yeni kayıt oluşturun.");
            }
        });
    }

    @FXML
    private void processSignUp() {
        try {
            String name = newName.getText();
            String email = newEmail.getText();
            String password = newPass.getText();
            String role = roleCombo.getValue();

            if (name.isEmpty() || role == null || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen tüm alanları doldurun!");
                return;
            }

            int newId = (int) (Math.random() * 9000) + 1000;
            User newUser;

            // ROL KONTROLÜ: Doğru nesneyi oluşturmak yetkiler için kritiktir
            if ("Teacher".equals(role)) {
                newUser = new Teacher(name, email, newId, password, "Genel", "Uzman");
            } else if ("Editor".equals(role)) {
                // Editor nesnesi oluşturuluyor (instanceof kontrolü artık bunu tanıyacak)
                newUser = new Editor(name, email, newId, password, "Genel", "Moderasyon");
            } else {
                newUser = new Student(name, email, newId, password, "Genel", "1. Sınıf");
            }

            DataStore.addUser(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Kayıt Başarılı", "Giriş ID'niz: " + newId + "\nNot almayı unutmayın.");
            showLogin();

        } catch (Exception e) {
            System.out.println("Kayıt hatası: " + e.getMessage());
        }
    }

    private void switchToMainScene(ActionEvent event, User user) throws IOException {
        String targetFXML = user.getRole().equals("Teacher") ? "mid2-view.fxml" : "mid1-view.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(targetFXML));
        Parent root = loader.load();

        if (user.getRole().equals("Teacher")) {
            Mid2Controller controller = loader.getController();
            controller.setUser(user);
        } else {
            Mid1Controller controller = loader.getController();
            controller.setUser(user);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void showHelp() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Destek ve İletişim");
        helpAlert.setHeaderText("Sistem Yöneticileri");

        // Mail adreslerini alt alta listeler
        String contactInfo = "omeroduncu@marun.edu.tr\n" +
                "emrearslan24@marun.edu.tr\n" +
                "eyüpboncuk@marun.edu.tr";

        helpAlert.setContentText(contactInfo);

        // Pencere stilini arayüze uygun hale getirmek istersen (isteğe bağlı)
        helpAlert.showAndWait();
    }
    @FXML private void showSignUp() { loginPane.setVisible(false); loginPane.setManaged(false); signupPane.setVisible(true); signupPane.setManaged(true); }
    @FXML private void showLogin() { signupPane.setVisible(false); signupPane.setManaged(false); loginPane.setVisible(true); loginPane.setManaged(true); }
}