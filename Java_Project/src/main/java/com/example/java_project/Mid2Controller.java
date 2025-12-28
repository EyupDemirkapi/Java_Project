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
    @FXML private ListView<String> myCreatedClassesListView;
    @FXML private ListView<String> joinedClassesListView;
    @FXML private TextField newClassNameField;
    @FXML private TextField classCodeField;

    private User currentUser;

    @FXML
    public void initialize() {
        // Kurduğum sınıflara tıklama dinleyicisi
        setupListViewListener(myCreatedClassesListView);
        // Katıldığım sınıflara tıklama dinleyicisi
        setupListViewListener(joinedClassesListView);
    }

    private void setupListViewListener(ListView<String> listView) {
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    // Kodun içinden ID'yi çekiyoruz: (Kod: 1234) kısmını yakalar
                    String classId = "";
                    if (newVal.contains("Kod: ")) {
                        classId = newVal.substring(newVal.indexOf("Kod: ") + 5, newVal.length() - 1);
                    }

                    Classroom selected = null;
                    for (Classroom c : DataStore.classrooms) {
                        if (c.getClassId().equals(classId)) {
                            selected = c;
                            break;
                        }
                    }

                    if (selected != null) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
                        Parent root = loader.load();
                        chatController controller = loader.getController();
                        controller.setChatData(currentUser, selected);

                        Stage stage = (Stage) listView.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setMaximized(true);
                        stage.show();
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }
        });
    }

    public void setUser(User user) {
        this.currentUser = user;
        refreshLists();
    }

    private void refreshLists() {
        if (currentUser == null) return;
        ObservableList<String> created = FXCollections.observableArrayList();
        ObservableList<String> joined = FXCollections.observableArrayList();

        for (Classroom c : DataStore.classrooms) {
            if (c.getTeacherId().equals(String.valueOf(currentUser.getID()))) {
                created.add(c.getClassName() + " (Kod: " + c.getClassId() + ")");
            } else if (c.getStudentIds().contains(currentUser.getID())) {
                joined.add(c.getClassName() + " (Kod: " + c.getClassId() + ")"); // Listener çalışması için formatı eşitledim
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
    private void handleJoinClass() {
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen(); // Giriş ekranı için ortala
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}