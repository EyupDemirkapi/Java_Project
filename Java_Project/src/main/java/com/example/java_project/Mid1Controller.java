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

public class Mid1Controller {
    @FXML private ListView<String> classListView;
    @FXML private TextField classCodeField;
    @FXML private ComboBox<String> academicYearCombo; // Yeni ComboBox

    private User currentUser;

    @FXML
    public void initialize() {
        // 1. ComboBox seçeneklerini doldur
        academicYearCombo.setItems(FXCollections.observableArrayList(
                "Hazırlık", "1. Sınıf", "2. Sınıf", "3. Sınıf", "4. Sınıf", "Mezun"
        ));

        // 2. Sınıf Listesi Tıklama Listener'ı
        classListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    String classId = "";
                    if (newVal.contains("(Kod: ")) {
                        int startIndex = newVal.lastIndexOf("(Kod: ") + 6;
                        int endIndex = newVal.lastIndexOf(")");
                        classId = newVal.substring(startIndex, endIndex).trim();
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
                        Stage stage = (Stage) classListView.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setUser(User user) {
        this.currentUser = user;
        refreshClassList();

        // Kullanıcı yüklendiğinde mevcut akademik yılını seçili getir
        if (currentUser instanceof Student) {
            academicYearCombo.setValue(((Student) currentUser).getAcademicYear());
        }
    }

    @FXML
    private void handleUpdateAcademicYear() {
        String selectedYear = academicYearCombo.getValue();
        if (selectedYear != null && currentUser instanceof Student) {
            // Nesneyi güncelle
            ((Student) currentUser).setAcademicYear(selectedYear);

            // Veriyi kalıcı olarak kaydet
            DataStore.saveAll();
            showAlert("Başarılı", "Akademik yılınız güncellendi: " + selectedYear);
        } else {
            showAlert("Hata", "Lütfen listeden bir yıl seçiniz!");
        }
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

    @FXML
    public void handleGoBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}