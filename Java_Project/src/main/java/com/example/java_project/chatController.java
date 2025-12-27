package com.example.java_project;

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
import java.util.Optional;

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
        alert.setTitle("Detaylar");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (msgObj instanceof Announcement) {
            Announcement ann = (Announcement) msgObj;
            alert.setContentText("Duyuru\nYazar: " + ann.getsomeAuthorName() + "\nTarih: " + ann.getsomeDate().format(dtf));
        } else {
            Comment comm = (Comment) msgObj;
            alert.setContentText("Yorum\nYazar: " + comm.getAuthorName() + "\nTarih: " + comm.getDate().format(dtf));
        }
        alert.showAndWait();
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
}