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
            // 1. "hello-view.fxml" yerine "login-view.fxml" yüklenmeli
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300); // Giriş ekranı boyutu

            // 2. Mevcut pencereyi (Stage) al
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Sahneyi değiştir
            stage.setScene(scene);
            stage.setTitle("Duyuru Sistemi - Giriş");
            stage.show();

        } catch (IOException e) {
            System.out.println("Giriş ekranı dosyası bulunamadı!");
            e.printStackTrace();
        }
    }
}

