package com.example.java_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class DataStore {
    // Listeleri "public" ve "static" bırakıyoruz ki her yerden erişilsin
    public static final ObservableList<User> users = FXCollections.observableArrayList();
    public static final ObservableList<Announcement> announcements = FXCollections.observableArrayList();
    public static final ObservableList<Comment> comments = FXCollections.observableArrayList();
    public static final ObservableList<Classroom> classrooms = FXCollections.observableArrayList();
    static {
        users.addAll(FileHandler.loadList("users.dat"));
        classrooms.addAll(FileHandler.loadList("classrooms.dat"));
        // Eğer dosya boşsa, test için bir hoca ekleyelim:
        if(users.isEmpty()) {
            users.add(new Teacher("Ahmet", "Hoca", 123, "123456", "Bilgisayar", "Prof. Dr."));
            saveAll(); // Dosyaya kaydet
        }

        announcements.addAll(FileHandler.loadList("announcements.dat"));
        comments.addAll(FileHandler.loadList("comments.dat"));
        // Dosyalar varsa yükle, yoksa boş liste döndürür
        users.addAll(FileHandler.loadList("users.dat"));
        announcements.addAll(FileHandler.loadList("announcements.dat"));
        comments.addAll(FileHandler.loadList("comments.dat"));
    }


    public static ObservableList<User> getUsers() {
        return users;
    }

    // --- KULLANICI İŞLEMLERİ ---
    public static void addUser(User user) {
        if (user != null) {
            users.add(user);
            saveAll();
        }
    }

    // --- DUYURU İŞLEMLERİ ---
    public static void addAnnouncement(Announcement ann) {
        announcements.add(ann);
        saveAll();
    }
    public static void deleteAnnouncement(Announcement ann) {

        comments.removeIf(comment -> comment.getAnnouncementId().equals(ann.getId()));
        announcements.remove(ann);
        saveAll();
    }

    // --- YORUM İŞLEMLERİ ---
    public static void addComment(Comment comment) {
        comments.add(comment);
        saveAll();
    }

    public static void deleteComment(Comment comment) {
        comments.remove(comment);
        saveAll();
    }

    // Merkezi Kayıt Metodu (Public yaptık)
    public static void saveAll() {
        FileHandler.saveList("users.dat", new ArrayList<>(users));
        FileHandler.saveList("announcements.dat", new ArrayList<>(announcements));
        FileHandler.saveList("comments.dat", new ArrayList<>(comments));
        FileHandler.saveList("classrooms.dat", new ArrayList<>(classrooms));
    }
}