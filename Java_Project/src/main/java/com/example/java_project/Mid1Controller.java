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
    public void handleGoBack(ActionEvent event) { // Parametre artık doğru (javafx)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Geri dönüş hatası: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        classListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Seçilen Sınıf: " + newVal);
            }
        });
    }
}