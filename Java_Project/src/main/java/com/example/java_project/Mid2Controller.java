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
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Iterator;

public class Mid2Controller {
    @FXML private ListView<String> myCreatedClassesListView;
    @FXML private ListView<String> joinedClassesListView;
    @FXML private TextField newClassNameField;
    @FXML private TextField classCodeField;

    private User currentUser;

    @FXML
    public void initialize() {
        // Tıklama olaylarını (Sol tık -> Chat) ayarla
        setupListViewListener(myCreatedClassesListView);
        setupListViewListener(joinedClassesListView);

        // Sağ tık menülerini (Sağ tık -> Sil) ayarla
        addDeleteContextMenu(myCreatedClassesListView);
        addDeleteContextMenu(joinedClassesListView);
    }

    private void addDeleteContextMenu(ListView<String> listView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Sınıfı Sil");
        deleteItem.setStyle("-fx-text-fill: red;");

        deleteItem.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                handleDeleteOrLeave(selected);
            }
        });

        contextMenu.getItems().add(deleteItem);
        listView.setContextMenu(contextMenu);
    }

    private void handleDeleteOrLeave(String selectedItem) {
        String classId = selectedItem.substring(selectedItem.indexOf("Kod: ") + 5, selectedItem.length() - 1);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bu sınıfı silmek veya ayrılmak istediğinize emin misiniz?", ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Iterator<Classroom> it = DataStore.classrooms.iterator();
                while (it.hasNext()) {
                    Classroom c = it.next();
                    if (c.getClassId().equals(classId)) {
                        // Eğitmense tamamen sil, öğrencisiyse listeden çık
                        if (c.getTeacherId().equals(String.valueOf(currentUser.getID()))) {
                            it.remove();
                        } else {
                            c.getStudentIds().remove(Integer.valueOf(currentUser.getID()));
                        }
                        DataStore.saveAll();
                        refreshLists();
                        break;
                    }
                }
            }
        });
    }

    private void setupListViewListener(ListView<String> listView) {
        listView.setOnMouseClicked(event -> {
            // Sadece farenin SOL tuşuyla tıklandığında (PRIMARY) chat'e gider
            if (event.getButton() == MouseButton.PRIMARY) {
                String newVal = listView.getSelectionModel().getSelectedItem();
                if (newVal != null) {
                    try {
                        String classId = newVal.substring(newVal.indexOf("Kod: ") + 5, newVal.length() - 1);
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
            }
            // Sağ tıklandığımızda bu blok çalışmaz, sadece ContextMenu görünür.
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
                joined.add(c.getClassName() + " (Kod: " + c.getClassId() + ")");
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
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}