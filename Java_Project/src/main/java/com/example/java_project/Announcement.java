package com.example.java_project;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String content;
    private String authorName;
    private int authorId; // YENİ: Sağa/Sola yaslama kontrolü için eklendi
    private LocalDateTime date;

    public Announcement(String title, String content, String authorName, int authorId) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorId = authorId;
        this.date = LocalDateTime.now();
    }

    // --- GETTER METODLARI ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getsomeContent() { return content; }
    public String getsomeAuthorName() { return authorName; }
    public int getAuthorId() { return authorId; } // YENİ
    public LocalDateTime getsomeDate() { return date; }

    public void setsomeContent(String content) { this.content = content; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public String toString() {
        return "[" + title + "] - " + authorName + " (" + date + ")";
    }
}