package com.example.java_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class Mid2Controller {
    // FXML Bileşenleri
    @FXML private ListView<String> myCreatedClassesListView; // Öğretmenin kurduğu
    @FXML private ListView<String> joinedClassesListView;    // Öğretmenin katıldığı
    @FXML private TextField newClassNameField;
    @FXML private TextField classCodeField;

    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        refreshLists();
    }

    private void refreshLists() {
        if (currentUser == null) return;

        ObservableList<String> created = FXCollections.observableArrayList();
        ObservableList<String> joined = FXCollections.observableArrayList();

        for (Classroom c : DataStore.classrooms) {
            // 1. Kendi kurduğu sınıflar
            if (c.getTeacherId().equals(String.valueOf(currentUser.getID()))) {
                created.add(c.getClassName() + " (Kod: " + c.getClassId() + ")");
            }
            // 2. Katıldığı (öğrenci/üye olarak) sınıflar
            else if (c.getStudentIds().contains(currentUser.getID())) {
                joined.add(c.getClassName() + " - " + c.getTeacherId());
            }
        }
        myCreatedClassesListView.setItems(created);
        joinedClassesListView.setItems(joined);
    }

    @FXML
    private void handleCreateClass() {
        String name = newClassNameField.getText().trim();
        if (!name.isEmpty()) {
            Classroom newClass = new Classroom(name, String.valueOf(currentUser.getID()));
            DataStore.classrooms.add(newClass);
            DataStore.saveAll();
            newClassNameField.clear();
            refreshLists();
        }
    }

    @FXML
    private void handleJoinClass() { // Mid1'deki mantığın aynısı
        String code = classCodeField.getText().trim().toUpperCase();
        for (Classroom c : DataStore.classrooms) {
            if (c.getClassId().equals(code)) {
                if (!c.getStudentIds().contains(currentUser.getID())) {
                    c.addStudent(currentUser.getID());
                    DataStore.saveAll();
                    refreshLists();
                    classCodeField.clear();
                    return;
                }
            }
        }
    }

    @FXML
    public void handleGoBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();
            MainController mc = loader.getController();
            mc.setUser(currentUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}