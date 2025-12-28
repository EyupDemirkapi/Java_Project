package com.example.java_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class DataStore {
    public static final ObservableList<User> users = FXCollections.observableArrayList();
    public static final ObservableList<Announcement> announcements = FXCollections.observableArrayList();
    public static final ObservableList<Comment> comments = FXCollections.observableArrayList();
    public static final ObservableList<Classroom> classrooms = FXCollections.observableArrayList();

    static {
        loadFromDisk();
    }

    public static void loadFromDisk() {
        // setAll kullanarak listenin her açılışta katlanarak şişmesini engelliyoruz
        users.setAll(FileHandler.loadList("users.dat"));
        classrooms.setAll(FileHandler.loadList("classrooms.dat"));
        announcements.setAll(FileHandler.loadList("announcements.dat"));
        comments.setAll(FileHandler.loadList("comments.dat"));

        if(users.isEmpty()) {
            users.add(new Teacher("Ahmet", "Hoca", 123, "123456", "Bilgisayar", "Prof. Dr."));
            saveAll();
        }
    }



    public static void saveAll() {
        FileHandler.saveList("users.dat", new ArrayList<>(users));
        FileHandler.saveList("announcements.dat", new ArrayList<>(announcements));
        FileHandler.saveList("comments.dat", new ArrayList<>(comments));
        FileHandler.saveList("classrooms.dat", new ArrayList<>(classrooms));
    }

    public static void clearAllData() {
        users.clear();
        announcements.clear();
        comments.clear();
        classrooms.clear();
        saveAll();
    }

    public static void addUser(User user) {
        if (user != null) {
            users.add(user);
            saveAll();
        }
    }

    public static ObservableList<User> getUsers() {
        return users;
    }
}