package com.example.java_project;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Teacher extends User implements IAnnouncer {
    // JavaFX TableView ve Binding işlemleri için Property kullanımı
    private final StringProperty expertise = new SimpleStringProperty();

    public Teacher() {
        super();
    }

    // Tam kapsamlı Constructor: Üst sınıfa (User) bilgileri gönderir
    public Teacher(String name, String surname, int id, String password, String department, String expertise) {
        super(name, surname, id, password, department);
        setExpertise(expertise);
    }

    // --- GETTER, SETTER ve PROPERTY METODLARI ---

    public String getExpertise() {
        return expertise.get();
    }

    public void setExpertise(String expertise) {
        this.expertise.set(expertise);
    }

    public StringProperty expertiseProperty() {
        return expertise;
    }

    // Konsol çıktılarını anlamlı kılmak için toString metodunu eziyoruz (Override)
    @Override
    public String toString() {
        return super.toString() + " [Uzmanlık: " + getExpertise() + "]";
    }

    @Override
    public void postAnnouncement(String title, String content) {
        System.out.println("Öğretmen Duyurusu: [" + title + "] - " + content);
    }
}
