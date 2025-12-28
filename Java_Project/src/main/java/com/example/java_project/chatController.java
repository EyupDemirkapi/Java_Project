package com.example.java_project;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class chatController {
    @FXML private VBox chatContainer;
    @FXML private TextArea messageArea;
    @FXML private Label classNameLabel;
    @FXML private ScrollPane chatScrollPane;
    @FXML private ComboBox<String> typeComboBox;

    private User currentUser;
    private Classroom currentClass;

    public void setChatData(User user, Classroom classroom) {
        // Kullanıcı ve sınıf atamasını yapıyoruz
        this.currentUser = user;
        this.currentClass = classroom;

        // Sınıf adını etikete yazdır
        classNameLabel.setText(classroom.getClassName());

        // ComboBox seçeneklerini doldur
        typeComboBox.setItems(FXCollections.observableArrayList("Yorum", "Duyuru"));
        typeComboBox.setValue("Yorum");

        // --- TEMA GÜNCELLEMESİ (Turkuaz Stil) ---
        // "Bilgiyi Güncelle" butonuyla aynı renk (#03dac6)
        typeComboBox.setStyle(
                "-fx-background-color: #03dac6; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
        );

        // --- KRİTİK ROL KONTROLÜ VE YETKİLENDİRME ---
        // .equalsIgnoreCase kullanarak büyük/küçük harf hatalarını engelliyoruz
        String role = currentUser.getRole();

        if ("Teacher".equalsIgnoreCase(role) || "Editor".equalsIgnoreCase(role)) {
            // Editör veya Öğretmen ise Duyuru ComboBox'ını göster
            typeComboBox.setVisible(true);
            typeComboBox.setManaged(true);

            // Debug için konsola çıktı alalım (Sorun devam ederse buradan kontrol edebilirsin)
            System.out.println("Yetkili Girişi: " + currentUser.getName() + " - Rol: " + role);
        } else {
            // Sadece Öğrenci ise ComboBox'ı tamamen gizle
            typeComboBox.setVisible(false);
            typeComboBox.setManaged(false);
            System.out.println("Öğrenci Girişi: " + currentUser.getName() + " - Rol: " + role);
        }

        // Kullanıcının son ziyaretini güncelle ve kaydet
        currentUser.setLastVisit(System.currentTimeMillis());
        DataStore.saveAll();

        // Mesajları listele
        refreshChat();
    }

    private void refreshChat() {
        chatContainer.getChildren().clear();
        chatContainer.setSpacing(10);

        List<Object> allMessages = new ArrayList<>();
        allMessages.addAll(currentClass.getAnnouncements());
        allMessages.addAll(currentClass.getComments());

        allMessages.sort((o1, o2) -> {
            LocalDateTime date1 = (o1 instanceof Announcement) ? ((Announcement) o1).getsomeDate() : ((Comment) o1).getDate();
            LocalDateTime date2 = (o2 instanceof Announcement) ? ((Announcement) o2).getsomeDate() : ((Comment) o2).getDate();
            return date1.compareTo(date2);
        });

        for (Object msg : allMessages) {
            addMessageBubble(msg);
        }
        scrollToBottom();
    }

    private void addMessageBubble(Object msgObj) {
        HBox container = new HBox();
        VBox bubble = new VBox(5);
        bubble.setMaxWidth(400);
        bubble.setStyle("-fx-padding: 10; -fx-background-radius: 15;");

        String displayAuthor, displayContent, displayRole;
        int msgAuthorId;

        if (msgObj instanceof Announcement) {
            Announcement ann = (Announcement) msgObj;
            displayAuthor = ann.getsomeAuthorName();
            displayContent = ann.getsomeContent();
            displayRole = "DUYURU";
            msgAuthorId = ann.getAuthorId();
            bubble.setStyle(bubble.getStyle() + "-fx-background-color: #c0392b;"); // KIRMIZI
        } else {
            Comment comm = (Comment) msgObj;
            displayAuthor = comm.getAuthorName();
            displayContent = comm.getContent();
            displayRole = comm.getAuthorRole();
            msgAuthorId = comm.getAuthorId();
            bubble.setStyle(bubble.getStyle() + "-fx-background-color: #34495e;"); // KOYU MAVİ
        }

        // WhatsApp Stili Yaslama
        if (msgAuthorId == currentUser.getID()) {
            container.setAlignment(Pos.CENTER_RIGHT);
            bubble.setStyle(bubble.getStyle() + "-fx-background-radius: 15 15 2 15;");
        } else {
            container.setAlignment(Pos.CENTER_LEFT);
            bubble.setStyle(bubble.getStyle() + "-fx-background-radius: 15 15 15 2;");
        }

        Label header = new Label(displayAuthor + " [" + displayRole + "]");
        header.setTextFill(javafx.scene.paint.Color.web("#f1c40f"));
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");

        Label content = new Label(displayContent);
        content.setTextFill(javafx.scene.paint.Color.WHITE);
        content.setWrapText(true);

        bubble.getChildren().addAll(header, content);
        bubble.setOnMouseClicked(e -> { if (e.getClickCount() == 2) handleMessageAction(msgObj); });

        container.getChildren().add(bubble);
        chatContainer.getChildren().add(container);
    }

    private void handleMessageAction(Object msgObj) {
        List<String> choices = new ArrayList<>();
        choices.add("Detayları Gör");

        boolean isAuthorized = false;
        String role = currentUser.getRole();

        if (msgObj instanceof Comment) {
            Comment c = (Comment) msgObj;
            // Kendi mesajı VEYA Editor VEYA Teacher ise tam yetkili
            if (currentUser.getID() == c.getAuthorId() || "Editor".equalsIgnoreCase(role) || "Teacher".equalsIgnoreCase(role)) {
                isAuthorized = true;
            }
        } else if (msgObj instanceof Announcement) {
            // Duyuruları sadece Editor ve Teacher silebilir/düzenleyebilir
            if ("Editor".equalsIgnoreCase(role) || "Teacher".equalsIgnoreCase(role)) {
                isAuthorized = true;
            }
        }

        if (isAuthorized) {
            choices.add("Mesajı Düzenle");
            choices.add("Mesajı Sil");
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Detayları Gör", choices);
        dialog.setTitle("İşlem Menüsü");
        dialog.showAndWait().ifPresent(choice -> {
            if (choice.equals("Mesajı Düzenle")) openEditDialog(msgObj);
            else if (choice.equals("Mesajı Sil")) confirmAndDelete(msgObj);
            else if (choice.equals("Detayları Gör")) showDetailAlert(msgObj);
        });
    }

    @FXML
    private void handleSendMessage() {
        String msg = messageArea.getText().trim();
        if (msg.isEmpty()) return;

        String role = currentUser.getRole();

        // Seçim "Duyuru" ise ve yetki varsa Duyuru olarak ekle
        if ("Duyuru".equals(typeComboBox.getValue()) && ("Teacher".equalsIgnoreCase(role) || "Editor".equalsIgnoreCase(role))) {
            currentClass.addAnnouncement(new Announcement("Sınıf Duyurusu", msg, currentUser.getName(), currentUser.getID()));
        } else {
            // Aksi halde Yorum olarak ekle (Rol burada kaydedilir)
            currentClass.addComment(new Comment(msg, currentUser.getName(), currentUser.getID(), role, currentClass.getClassId()));
        }

        DataStore.saveAll();
        messageArea.clear();
        refreshChat();
    }

    // --- DİĞER YARDIMCI METODLAR (openEditDialog, confirmAndDelete, showDetailAlert aynı kalabilir) ---
    private void openEditDialog(Object msgObj) {
        String currentContent = (msgObj instanceof Announcement) ? ((Announcement) msgObj).getsomeContent() : ((Comment) msgObj).getContent();
        TextInputDialog dialog = new TextInputDialog(currentContent);
        dialog.setTitle("Mesajı Düzenle");
        dialog.showAndWait().ifPresent(newText -> {
            if (msgObj instanceof Announcement) ((Announcement) msgObj).setsomeContent(newText);
            else ((Comment) msgObj).setContent(newText);
            DataStore.saveAll();
            refreshChat();
        });
    }

    private void confirmAndDelete(Object msgObj) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Silmek istediğinize emin misiniz?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (msgObj instanceof Comment) currentClass.deleteComment((Comment) msgObj, currentUser);
                else currentClass.getAnnouncements().remove((Announcement) msgObj);
                DataStore.saveAll();
                refreshChat();
            }
        });
    }

    private void scrollToBottom() { Platform.runLater(() -> { if (chatScrollPane != null) chatScrollPane.setVvalue(1.0); }); }

    private void showDetailAlert(Object msgObj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mesaj Detayları");
        alert.setHeaderText(null);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String detailText = "";

        if (msgObj instanceof Announcement) {
            Announcement ann = (Announcement) msgObj;
            // Duyuruyu yazan kullanıcıyı sistemden bulup mailini alıyoruz
            User author = DataStore.getUsers().stream()
                    .filter(u -> u.getID() == ann.getAuthorId())
                    .findFirst().orElse(null);

            String email = (author != null) ? author.getEmail() : "Bilinmiyor";

            detailText = "--- DUYURU ---\n" +
                    "Yazar: " + ann.getsomeAuthorName() + "\n" +
                    "E-posta: " + email + "\n" + // Mail buraya eklendi
                    "Tarih: " + ann.getsomeDate().format(dtf) + "\n" +
                    "İçerik: " + ann.getsomeContent();
        } else {
            Comment comm = (Comment) msgObj;
            // Yorumu yazan kullanıcıyı sistemden bulup mailini alıyoruz
            User author = DataStore.getUsers().stream()
                    .filter(u -> u.getID() == comm.getAuthorId())
                    .findFirst().orElse(null);

            String email = (author != null) ? author.getEmail() : "Bilinmiyor";

            detailText = "--- YORUM ---\n" +
                    "Yazar: " + comm.getAuthorName() + " [" + comm.getAuthorRole() + "]\n" +
                    "E-posta: " + email + "\n" + // Mail buraya eklendi
                    "Tarih: " + comm.getDate().format(dtf) + "\n" +
                    "İçerik: " + comm.getContent();
        }

        alert.setContentText(detailText);
        alert.showAndWait();
    }
    @FXML
    private void handleUpdateUserInfo() {
        // 1. Özel bir Dialog penceresi oluşturuyoruz
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Profilimi Güncelle");
        dialog.setHeaderText("ID: " + currentUser.getID() + " - Bilgilerinizi Güncelleyin");

        // 2. Butonları Ayarla (Tamam ve İptal)
        ButtonType updateButtonType = new ButtonType("Güncelle", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // 3. Giriş alanlarını oluştur (Layout)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Yeni E-posta");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Yeni Şifre (Boş bırakılırsa değişmez)");

        grid.add(new Label("E-posta:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Yeni Şifre:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // 4. Sonuçları listeye dönüştür
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                List<String> results = new ArrayList<>();
                results.add(emailField.getText());
                results.add(passwordField.getText());
                return results;
            }
            return null;
        });

        // 5. Verileri güncelle ve kaydet
        dialog.showAndWait().ifPresent(results -> {
            String newEmail = results.get(0);
            String newPass = results.get(1);

            if (!newEmail.isEmpty()) {
                currentUser.setEmail(newEmail);
            }

            // Şifre alanı boş değilse ve en az 6 karakterse güncelle
            if (!newPass.isEmpty()) {
                if (newPass.length() >= 6) {
                    currentUser.setPassword(newPass);
                } else {
                    showAlert(Alert.AlertType.WARNING, "Hata", "Şifre en az 6 karakter olmalıdır!");
                    return;
                }
            }

            DataStore.saveAll(); // Değişiklikleri users.dat dosyasına yazar
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Profiliniz güncellendi.");
        });
    }
    @FXML
    public void handleGoBack(ActionEvent event) throws IOException {
        String fxml = "Teacher".equalsIgnoreCase(currentUser.getRole()) ? "mid2-view.fxml" : "mid1-view.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        if("Teacher".equalsIgnoreCase(currentUser.getRole())) ((Mid2Controller)loader.getController()).setUser(currentUser);
        else ((Mid1Controller)loader.getController()).setUser(currentUser);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
