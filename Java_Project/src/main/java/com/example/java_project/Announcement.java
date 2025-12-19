package com.example.java_project;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String content;
    private String authorName; // Duyuruyu yapanın adı
    private LocalDateTime date;

    public Announcement(String title, String content, String authorName) {
        this.id = UUID.randomUUID().toString(); // Her duyuruya benzersiz otomatik ID
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.date = LocalDateTime.now();
    }

    // GETTER METODLARI
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public LocalDateTime getDate() { return date; }

    @Override
    public String toString() {
        return "[" + title + "] - " + authorName + " (" + date + ")";
    }
}
