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
        // Classroom içindeki listenin adı getComments() olarak varsayıldı
        for (Comment comm : currentClass.getComments()) {
            addMessageBubble(comm);
        }
    }

    private void addMessageBubble(Comment comm) {
        VBox bubble = new VBox(5);
        bubble.setStyle("-fx-background-color: #34495e; -fx-padding: 10; -fx-background-radius: 10;");

        // İsim ve Rol Bilgisi (Comment sınıfındaki metodlar)
        Label header = new Label(comm.getAuthorName() + " [" + comm.getAuthorRole() + "]");
        header.setTextFill(Color.web("#f39c12"));
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");

        // 'getContent' kullanımı (Comment sınıfındaki değişken adı)
        Label content = new Label(comm.getContent());
        content.setTextFill(Color.WHITE);
        content.setWrapText(true);

        bubble.getChildren().addAll(header, content);

        // DÜZENLEME YETKİSİ KONTROLÜ
        boolean hasPermission = currentUser.getRole().equals("Editor") ||
                comm.getAuthorId() == currentUser.getID();

        if (hasPermission) {
            Button editBtn = new Button("Düzenle");
            editBtn.setStyle("-fx-font-size: 9; -fx-background-color: #7f8c8d;");
            editBtn.setOnAction(e -> openEditDialog(comm));
            bubble.getChildren().add(editBtn);
        }

        chatContainer.getChildren().add(0, bubble);
    }

    @FXML
    private void handleSendMessage() {
        String msg = messageArea.getText().trim();
        if (!msg.isEmpty()) {
            // Comment sınıfı constructor'ına uygun olarak (content, name, id, role, announcementId)
            Comment newComm = new Comment(msg, currentUser.getName(), currentUser.getID(), currentUser.getRole(), currentClass.getClassId());
            currentClass.addComment(newComm);
            DataStore.saveAll();
            messageArea.clear();
            refreshChat();
        }
    }

    private void openEditDialog(Comment comm) {
        // 'getContent' ve 'setContent' metodları kullanıldı
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