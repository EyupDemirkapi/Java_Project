package com.example.java_project;

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
import java.time.format.DateTimeFormatter;

public class chatController {
    @FXML private VBox chatContainer;
    @FXML private TextArea messageArea;
    @FXML private Label classNameLabel;

    private User currentUser;
    private Classroom currentClass;

    public void setChatData(User user, Classroom classroom) {
        this.currentUser = user;
        this.currentClass = classroom;
        classNameLabel.setText(classroom.getClassName());
        refreshChat();
    }

    private void refreshChat() {
        chatContainer.getChildren().clear();

        // 1. Önce Duyuruları (Announcement) ekleyelim (Senin özel metodlarını kullanacak olanlar)
        for (Announcement ann : currentClass.getAnnouncements()) {
            addMessageBubble(ann);
        }

        // 2. Sonra Normal Yorumları (Comment) ekleyelim
        for (Comment comm : currentClass.getComments()) {
            addMessageBubble(comm);
        }
    }

    private void addMessageBubble(Object msgObj) {
        VBox bubble = new VBox(5);
        bubble.setStyle("-fx-padding: 10; -fx-background-radius: 10; -fx-cursor: hand;");

        String displayAuthor, displayContent, displayRole;

        // --- BURADA SENİN ÖZEL METODLARINI DEVREYE SOKUYORUZ ---
        if (msgObj instanceof Announcement) {
            Announcement ann = (Announcement) msgObj;
            displayAuthor = ann.getsomeAuthorName(); // SENİN METODUN
            displayContent = ann.getsomeContent();   // SENİN METODUN
            displayRole = "DUYURU";
            bubble.setStyle(bubble.getStyle() + "-fx-background-color: #e67e22;"); // Duyurular Turuncu
        } else {
            Comment comm = (Comment) msgObj;
            displayAuthor = comm.getAuthorName();
            displayContent = comm.getContent();
            displayRole = comm.getAuthorRole();
            bubble.setStyle(bubble.getStyle() + "-fx-background-color: #34495e;"); // Yorumlar Mavi
        }

        Label header = new Label(displayAuthor + " [" + displayRole + "]");
        header.setTextFill(Color.web("#f1c40f"));
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");

        Label content = new Label(displayContent);
        content.setTextFill(Color.WHITE);
        content.setWrapText(true);

        bubble.getChildren().addAll(header, content);

        // --- SAĞ TIK MENÜSÜ (ÖZEL METODLARI KULLANAN KISIM) ---
        ContextMenu contextMenu = new ContextMenu();
        MenuItem infoItem = new MenuItem("Detayları Görüntüle");

        infoItem.setOnAction(e -> showDetailAlert(msgObj));
        contextMenu.getItems().add(infoItem);

        bubble.setOnContextMenuRequested(e ->
                contextMenu.show(bubble, e.getScreenX(), e.getScreenY())
        );

        // Düzenleme Yetkisi (Sadece Comment'ler için veya Duyuru sahibi için)
        checkPermissionsAndAddEditButton(bubble, msgObj);

        chatContainer.getChildren().add(0, bubble);
    }

    private void showDetailAlert(Object msgObj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sistem Bilgisi");
        alert.setHeaderText("Gönderi Detaylı Verisi");

        String detailText;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        if (msgObj instanceof Announcement) {
            Announcement ann = (Announcement) msgObj;
            // SENİN METODLARININ BİZZAT KULLANILDIĞI YER:
            detailText = "TİP: RESMİ DUYURU\n" +
                    "YAZAR: " + ann.getsomeAuthorName() + "\n" +
                    "ZAMAN: " + ann.getsomeDate().format(dtf) + "\n" +
                    "İÇERİK: " + ann.getsomeContent();
        } else {
            Comment comm = (Comment) msgObj;
            detailText = "TİP: ÖĞRENCİ YORUMU\n" +
                    "YAZAR: " + comm.getAuthorName() + "\n" +
                    "ZAMAN: " + comm.getDate().format(dtf) + "\n" +
                    "İÇERİK: " + comm.getContent();
        }

        alert.setContentText(detailText);
        alert.showAndWait();
    }

    private void checkPermissionsAndAddEditButton(VBox bubble, Object msgObj) {
        // Sadece Comment nesneleri düzenlenebilir varsayıyoruz
        if (msgObj instanceof Comment) {
            Comment comm = (Comment) msgObj;
            if (currentUser.getRole().equals("Editor") || comm.getAuthorId() == currentUser.getID()) {
                Button editBtn = new Button("Düzenle");
                editBtn.setStyle("-fx-font-size: 9; -fx-background-color: #7f8c8d;");
                editBtn.setOnAction(e -> openEditDialog(comm));
                bubble.getChildren().add(editBtn);
            }
        }
    }

    @FXML
    private void handleSendMessage() {
        String msg = messageArea.getText().trim();
        if (msg.isEmpty()) return;

        // KONTROL: Eğer kullanıcı Teacher (Hoca) veya Editor ise Duyuru oluştur
        if (currentUser.getRole().equals("Teacher") || currentUser.getRole().equals("Editor")) {
            // Senin Announcement sınıfındaki constructor'a göre: title, content, authorName
            Announcement newAnn = new Announcement("Sınıf Duyurusu", msg, currentUser.getName());
            currentClass.addAnnouncement(newAnn); // Classroom'a duyuru olarak ekle
        } else {
            // Öğrenci ise normal Yorum (Comment) oluştur
            Comment newComm = new Comment(msg, currentUser.getName(), currentUser.getID(), currentUser.getRole(), currentClass.getClassId());
            currentClass.addComment(newComm);
        }

        DataStore.saveAll();
        messageArea.clear();
        refreshChat(); // Listeyi yenileyince artık hocanınki turuncu, öğrencininki mavi görünecek
    }

    private void openEditDialog(Comment comm) {
        TextInputDialog dialog = new TextInputDialog(comm.getContent());
        dialog.setTitle("Düzenle");
        dialog.setHeaderText("Mesajınızı güncelleyin:");
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

        if(currentUser.getRole().equals("Teacher")) {
            ((Mid2Controller)loader.getController()).setUser(currentUser);
        } else {
            ((Mid1Controller)loader.getController()).setUser(currentUser);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}