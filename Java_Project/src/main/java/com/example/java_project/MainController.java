package com.example.java_project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class MainController {
    private User currentUser;

    @FXML private Label welcomeLabel;
    @FXML private Button addAnnouncementBtn; // FXML tarafında tanımladığın buton

    // Dışarıdan kullanıcıyı set etmek için (Login'den gelirken)
    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Hoş geldin, " + user.getName());

        // Yetki kontrolü: Eğer kullanıcı duyuru yapamazsa butonu gizle
        if (!(user instanceof IAnnouncer)) {
            if (addAnnouncementBtn != null) {
                addAnnouncementBtn.setVisible(false);
            }
        }
    }

    // İhtiyacın olan getUser metodu:
    public User getCurrentUser() {
        return currentUser;
    }
}