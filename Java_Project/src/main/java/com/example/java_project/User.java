package com.example.java_project;

import javafx.beans.property.*;

public class User {
    // JavaFX TableView ve Binding işlemleri için Property kullanımı
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty surname = new SimpleStringProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty department = new SimpleStringProperty();
    private final IntegerProperty lastVisit = new SimpleIntegerProperty();

    // Parametresiz Constructor
    public User() {}

    // Parametreli Constructor
    public User(String name, String surname, int id, String password, String department) {
        setName(name);
        setSurname(surname);
        this.id.set(id); // ID genelde constructor ile bir kez set edilir
        setPassword(password);
        setDepartment(department);
    }

    // --- GETTER, SETTER ve PROPERTY METODLARI ---

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getSurname() { return surname.get(); }
    public void setSurname(String surname) { this.surname.set(surname); }
    public StringProperty surnameProperty() { return surname; }

    public int getID() { return id.get(); }
    // setID metodunu bilerek eklemedik (ID değişmez kabul edildi)
    public IntegerProperty idProperty() { return id; }

    public String getPassword() { return password.get(); }
    public void setPassword(String password) {
        // Güvenlik Kontrolü: Şifre en az 6 karakter olmalı
        if (password != null && password.length() >= 6) {
            this.password.set(password);
        } else {
            System.out.println("Hata: Şifre en az 6 karakter olmalıdır!");
        }
    }
    public StringProperty passwordProperty() { return password; }

    public String getDepartment() { return department.get(); }
    public void setDepartment(String department) { this.department.set(department); }
    public StringProperty departmentProperty() { return department; }

    public int getLastVisit() { return lastVisit.get(); }
    public void setLastVisit(int lastVisit) { this.lastVisit.set(lastVisit); }
    public IntegerProperty lastVisitProperty() { return lastVisit; }

    // --- toString METODU ---
    // Konsolda nesneyi yazdırdığında anlamlı veri görmeni sağlar
    @Override
    public String toString() {
        return "User{" +
                "ID=" + getID() +
                ", Ad='" + getName() + '\'' +
                ", Soyad='" + getSurname() + '\'' +
                ", Departman='" + getDepartment() + '\'' +
                '}';
    }
}