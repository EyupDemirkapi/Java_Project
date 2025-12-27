package com.example.java_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    private User currentUser;
    private Classroom currentClass;

    public void setChatData(User user, Classroom classroom) {
        this.currentUser = user;
        this.currentClass = classroom;
        classNameLabel.setText(classroom.getClassName());

        // Kullanıcı girişiyle LastVisit güncelle (User'da long olmalı)
        currentUser.setLastVisit(System.currentTimeMillis());
        DataStore.saveAll();

        refreshChat();
    }

    /**
     * Tüm mesajları kronolojik (zamana göre) sıraya koyar ve ekrana basar.
     * Bu sayede duyurular ve yorumlar karışık ve en yeni en altta olacak şekilde görünür.
     */
    private void refreshChat() {
        chatContainer.getChildren().clear();

        // 1. Tüm mesajları tek bir listede birleştir
        List<Object> allMessages = new ArrayList<>();
        allMessages.addAll(currentClass.getAnnouncements());
        allMessages.addAll(currentClass.getComments());

        // 2. Mesajları tarihlerine göre sırala (Eskiden Yeniye)
        allMessages.sort((o1, o2) -> {
            LocalDateTime date1 = (o1 instanceof Announcement) ?
                    ((Announcement) o1).getsomeDate() : ((Comment) o1).getDate();
            LocalDateTime date2 = (o2 instanceof Announcement) ?
                    ((Announcement) o2).getsomeDate() : ((Comment) o2).getDate();
            return date1.compareTo(date2);
        });

        // 3. Sıralı listeyi ekrana bas
        for (Object msg : allMessages) {
            addMessageBubble(msg);
        }

        scrollToBottom();
    }

    private void addMessageBubble(Object msgObj) {
        VBox bubble = new VBox(5);
        bubble.setStyle("-fx-padding: 10; -fx-background-radius: 10; -fx-cursor: hand;");

        String displayAuthor, displayContent, displayRole;

        if (msgObj instanceof Announcement) {
            Announcement ann = (Announcement) msgObj;
            displayAuthor = ann.getsomeAuthorName();
            displayContent = ann.getsomeContent();
            displayRole = "DUYURU";
            bubble.setStyle(bubble.getStyle() + "-fx-background-color: #e67e22;"); // Turuncu
        } else {
            Comment comm = (Comment) msgObj;
            displayAuthor = comm.getAuthorName();
            displayContent = comm.getContent();
            displayRole = comm.getAuthorRole();
            bubble.setStyle(bubble.getStyle() + "-fx-background-color: #34495e;"); // Mavi
        }

        Label header = new Label(displayAuthor + " [" + displayRole + "]");
        header.setTextFill(Color.web("#f1c40f"));
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");

        Label content = new Label(displayContent);
        content.setTextFill(Color.WHITE);
        content.setWrapText(true);

        bubble.getChildren().addAll(header, content);

        // Çift Tıklama Olayı
        bubble.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleMessageAction(msgObj);
            }
        });

        checkPermissionsAndAddEditButton(bubble, msgObj);

        // Mesajı listenin en sonuna ekle (Aşağı doğru akış)
        chatContainer.getChildren().add(bubble);
    }

    @FXML
    private void handleSendMessage() {
        String msg = messageArea.getText().trim();
        if (msg.isEmpty()) return;

        // Hoca/Editör '!' ile başlıyorsa Duyuru, aksi halde Yorum yapar
        if (msg.startsWith("!") && (currentUser.getRole().equals("Teacher") || currentUser.getRole().equals("Editor"))) {
            String cleanMsg = msg.substring(1).trim();
            Announcement newAnn = new Announcement("Sınıf Duyurusu", cleanMsg, currentUser.getName());
            currentClass.addAnnouncement(newAnn);
        } else {
            Comment newComm = new Comment(msg, currentUser.getName(), currentUser.getID(), currentUser.getRole(), currentClass.getClassId());
            currentClass.addComment(newComm);
        }

        DataStore.saveAll();
        messageArea.clear();
        refreshChat();
    }

    private void handleMessageAction(Object msgObj) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Detayları Gör", "Detayları Gör", "Mesajı Sil");
        dialog.setTitle("Mesaj İşlemleri");
        dialog.setHeaderText("İşlem Seçin");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(choice -> {
            if (choice.equals("Detayları Gör")) {
                showDetailAlert(msgObj);
            } else if (choice.equals("Mesajı Sil")) {
                confirmAndDelete(msgObj);
            }
        });
    }

    private void confirmAndDelete(Object msgObj) {
        boolean canDelete = false;
        if (msgObj instanceof Comment) {
            Comment c = (Comment) msgObj;
            if (currentUser.getID() == c.getAuthorId() || currentUser.getRole().equals("Editor")) canDelete = true;
        } else if (msgObj instanceof Announcement) {
            if (currentUser.getRole().equals("Teacher") || currentUser.getRole().equals("Editor")) canDelete = true;
        }

        if (!canDelete) {
            new Alert(Alert.AlertType.ERROR, "Yetkiniz yok!").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Silinsin mi?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (msgObj instanceof Comment) {
                    currentClass.deleteComment((Comment) msgObj, currentUser);
                } else {
                    currentClass.getAnnouncements().remove((Announcement) msgObj);
                }
                DataStore.saveAll();
                refreshChat();
            }
        });
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            if (chatScrollPane != null) {
                chatScrollPane.setVvalue(1.0);
            }
        });
    }

    private void showDetailAlert(Object msgObj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sistem Bilgisi");
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

    private void checkPermissionsAndAddEditButton(VBox bubble, Object msgObj) {
        if (msgObj instanceof Comment) {
            Comment comm = (Comment) msgObj;
            if (currentUser.getRole().equals("Editor") || comm.getAuthorId() == currentUser.getID()) {
                Button editBtn = new Button("Düzenle");
                editBtn.setStyle("-fx-font-size: 9;");
                editBtn.setOnAction(e -> openEditDialog(comm));
                bubble.getChildren().add(editBtn);
            }
        }
    }

    private void openEditDialog(Comment comm) {
        TextInputDialog dialog = new TextInputDialog(comm.getContent());
        dialog.showAndWait().ifPresent(newText -> {
            comm.setContent(newText);
            DataStore.saveAll();
            refreshChat();
        });
    }

    @FXML
    public void handleGoBack(ActionEvent event) throws IOException {
        String fxml = currentUser.getRole().equals("Teacher") ? "mid2-view.fxml" : "mid1-view.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        if(currentUser.getRole().equals("Teacher")) ((Mid2Controller)loader.getController()).setUser(currentUser);
        else ((Mid1Controller)loader.getController()).setUser(currentUser);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}