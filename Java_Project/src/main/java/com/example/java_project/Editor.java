package com.example.java_project;

public class Editor extends User  {
    private String expertise;

    public Editor() {
        super();
    }

    public Editor(String name, String surname, int id, String password, String department, String expertise) {
        super(name, surname, id, password, department); //
        this.expertise = expertise;
    }
    @Override
    public String getRole() {
        return "Editor";
    }

    @Override
    public String toString() {
        return super.toString() + " [Uzmanlık: " + expertise + "]";
    }
}