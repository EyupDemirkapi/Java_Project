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

public class Mid1Controller {
    @FXML private ListView<String> classListView;
    @FXML private TextField classCodeField;
    @FXML private ComboBox<String> academicYearCombo;

    private User currentUser;

    @FXML
    public void initialize() {
        // 1. ComboBox seçeneklerini doldur
        academicYearCombo.setItems(FXCollections.observableArrayList(
                "Hazırlık", "1. Sınıf", "2. Sınıf", "3. Sınıf", "4. Sınıf", "Mezun"
        ));

        // ComboBox Görsel Stili
        academicYearCombo.setStyle(
                "-fx-background-color: #03dac6; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
        );

        academicYearCombo.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextFill(javafx.scene.paint.Color.BLACK);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        // 2. Sağ Tık Menüsünü Hazırla
        setupContextMenu();

        // 3. TIKLAMA AYRIMI: Sol tık chat açar, sağ tık sadece menüyü gösterir
        classListView.setOnMouseClicked(event -> {
            // Sadece farenin SOL tuşuyla tıklandığında (PRIMARY) chat'e gider
            if (event.getButton() == MouseButton.PRIMARY) {
                String selected = classListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openChat(selected);
                }
            }
            // Sağ tıklandığında (SECONDARY) bu blok çalışmaz, sadece ContextMenu görünür.
        });
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Sınıfı Sil / Ayrıl");
        deleteItem.setStyle("-fx-text-fill: red;");

        deleteItem.setOnAction(e -> {
            String selected = classListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                handleDeleteClass(selected);
            }
        });

        contextMenu.getItems().add(deleteItem);
        classListView.setContextMenu(contextMenu);
    }

    private void handleDeleteClass(String selectedItem) {
        String classId = extractId(selectedItem);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sınıftan ayrılmak istediğinize emin misiniz?", ButtonType.YES, ButtonType.NO);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                for (Classroom c : DataStore.classrooms) {
                    if (c.getClassId().equals(classId)) {
                        c.getStudentIds().remove(Integer.valueOf(currentUser.getID()));
                        DataStore.saveAll();
                        refreshClassList();
                        break;
                    }
                }
            }
        });
    }

    private String extractId(String text) {
        if (text != null && text.contains("(Kod: ")) {
            int startIndex = text.lastIndexOf("(Kod: ") + 6;
            int endIndex = text.lastIndexOf(")");
            return text.substring(startIndex, endIndex).trim();
        }
        return "";
    }

    private void openChat(String newVal) {
        try {
            String classId = extractId(newVal);
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
                stage.setMaximized(true); // Tam ekran geçişi
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        refreshClassList();
        if (currentUser instanceof Student) {
            academicYearCombo.setValue(((Student) currentUser).getAcademicYear());
        }
    }

    @FXML
    private void handleUpdateAcademicYear() {
        String selectedYear = academicYearCombo.getValue();
        if (selectedYear != null && currentUser instanceof Student) {
            ((Student) currentUser).setAcademicYear(selectedYear);
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
        if (code.isEmpty()) return;

        boolean found = false;
        for (Classroom c : DataStore.classrooms) {
            if (c.getClassId().equals(code)) {
                if (!c.getStudentIds().contains(currentUser.getID())) {
                    c.addStudent(currentUser.getID());
                    DataStore.saveAll();
                    refreshClassList();
                    classCodeField.clear();
                    showAlert("Başarılı", "Sınıfa başarıyla katıldınız!");
                } else {
                    showAlert("Uyarı", "Bu sınıfa zaten kayıtlısınız.");
                }
                found = true;
                break;
            }
        }
        if (!found) {
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