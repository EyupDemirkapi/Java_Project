package com.example.java_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class DataStore {
    // Listelerimizi tanımlıyoruz
    public static final ObservableList<User> users = FXCollections.observableArrayList();
    public static final ObservableList<Announcement> announcements = FXCollections.observableArrayList();
    public static final ObservableList<Comment> comments = FXCollections.observableArrayList();

    static {
        // Uygulama açıldığında tüm dosyaları yükle
        users.addAll(FileHandler.loadList("users.dat"));
        announcements.addAll(FileHandler.loadList("announcements.dat"));
        comments.addAll(FileHandler.loadList("comments.dat"));
    }

    // --- KULLANICI İŞLEMLERİ ---
    public static void addUser(User user) {
        users.add(user);
        saveAll(); // Her şey dahil kaydet
    }

    // --- DUYURU İŞLEMLERİ ---
    public static void addAnnouncement(Announcement ann) {
        announcements.add(ann);
        saveAll();
    }

    public static void deleteAnnouncement(Announcement ann) {
        // Duyuru silindiğinde ona ait yorumları da temizlemek (Cascade Delete)
        comments.removeIf(comment -> comment.getId().equals(ann.getId()));
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

    // Tüm listeleri dosyalara basan merkezi metod
    private static void saveAll() {
        FileHandler.saveList("users.dat", new ArrayList<>(users));
        FileHandler.saveList("announcements.dat", new ArrayList<>(announcements));
        FileHandler.saveList("comments.dat", new ArrayList<>(comments));
    }
}