package com.example.java_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent; // DOĞRU IMPORT BU: javafx olmalı
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Mid1Controller {
    @FXML private ListView<String> classListView;
    @FXML private TextField classCodeField;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        refreshClassList();
    }

    private void refreshClassList() {
        if (currentUser == null) return;

        ObservableList<String> joinedClasses = FXCollections.observableArrayList();

        for (Classroom c : DataStore.classrooms) {
            if (c.getStudentIds().contains(currentUser.getID())) {
                joinedClasses.add(c.getClassName() + " (Kod: " + c.getClassId() + ")");
            }
        }
        classListView.setItems(joinedClasses);
    }

    @FXML
    private void handleJoinClass() {
        String code = classCodeField.getText().trim().toUpperCase();

        boolean found = false;
        for (Classroom c : DataStore.classrooms) {
            if (c.getClassId().equals(code)) {
                c.addStudent(currentUser.getID());
                DataStore.saveAll();
                found = true;
                break;
            }
        }

        if (found) {
            showAlert("Başarılı", "Sınıfa başarıyla katıldınız!");
            refreshClassList();
            classCodeField.clear();
        } else {
            showAlert("Hata", "Geçersiz sınıf kodu!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleGoBack(ActionEvent event) {
        try {
            // "main-view.fxml" yerine "login-view.fxml" (veya senin ana giriş dosyanın adı) olmalı
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            // Mevcut pencereyi (Stage) al
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Yeni sahneyi set et ve göster
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen(); // Pencereyi ortalar
            stage.show();

        } catch (IOException e) {
            System.err.println("Geri dönüş hatası: FXML dosyası bulunamadı! " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        classListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    // Seçilen sınıf isminden Classroom nesnesini bul
                    Classroom selected = null;
                    for (Classroom c : DataStore.classrooms) {
                        if (newVal.contains(c.getClassId())) {
                            selected = c;
                            break;
                        }
                    }

                    if (selected != null) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
                        Parent root = loader.load();

                        chatController controller = loader.getController();
                        controller.setChatData(currentUser, selected);

                        Stage stage = (Stage) classListView.getScene().getWindow();
                        stage.setScene(new Scene(root));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}