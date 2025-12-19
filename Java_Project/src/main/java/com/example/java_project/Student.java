package com.example.java_project;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student extends User {
    // JavaFX uyumlu Property tanımı
    private final StringProperty academicYear = new SimpleStringProperty();

    public Student() {
        super();
    }

    // Tüm bilgileri alan kapsamlı Constructor
    public Student(String name, String surname, int id, String password, String department, String academicYear) {
        // User sınıfındaki constructor'ı çağırarak temel bilgileri set ediyoruz
        super(name, surname, id, password, department);
        setAcademicYear(academicYear);
    }

    // --- GETTER, SETTER ve PROPERTY ---

    public String getAcademicYear() {
        return academicYear.get();
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear.set(academicYear);
    }

    public StringProperty academicYearProperty() {
        return academicYear;
    }

    // User'daki toString'i geliştirerek öğrenciye özel bilgileri ekleyelim
    @Override
    public String toString() {
        return super.toString() + " [Akademik Yıl: " + getAcademicYear() + "]";
    }
}
