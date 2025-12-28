package com.example.java_project;

public class Teacher extends User  {
    // Serileştirme hatası almamak için düz String kullanıyoruz
    private String expertise;

    public Teacher() {
        super();
    }

    public Teacher(String name, String surname, int id, String password, String department, String expertise) {
        super(name, surname, id, password, department);
        this.expertise = expertise;
    }


    @Override
    public String toString() {
        return super.toString() + " [Hoca Uzmanlık: " + expertise + "]";
    }



}