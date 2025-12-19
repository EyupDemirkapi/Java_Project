package com.example.java_project;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    public void onDeleteClicked(User currentUser, Object itemToDelete) {
        if (itemToDelete instanceof Comment) { // Eğer silinen bir yorum nesnesiyse
            Comment selectedComment = (Comment) itemToDelete;

            // Kullanıcı kendi yorumunu veya yetkiliyse başkasının yorumunu siliyor
            DataStore.deleteComment(selectedComment);
            System.out.println("Yorum silindi: " + selectedComment.getContent());
        }
        else if (itemToDelete instanceof Announcement) { // Eğer silinen bir duyuru nesnesiyse
            if (currentUser instanceof IAnnouncer) {
                Announcement selectedAnn = (Announcement) itemToDelete;

                // IAnnouncer yetkisi olan kullanıcı duyuruyu siliyor
                ((IAnnouncer) currentUser).deleteAnnouncement(selectedAnn);

                // Merkezi veri deposundan ve dosyadan da siliyoruz
                DataStore.deleteAnnouncement(selectedAnn);
                System.out.println("Duyuru ve bağlı yorumlar silindi: " + selectedAnn.getTitle());
            } else {
                System.out.println("Hata: Duyuru silme yetkiniz yok!");
            }
        }
    }
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

