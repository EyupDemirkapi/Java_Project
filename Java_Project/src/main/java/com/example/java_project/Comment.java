package com.example.java_project;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String content;
    private String authorName;
    private String announcementId;
    private LocalDateTime date;
    private int authorId;
    private String authorRole;

    // YENİ: chatController'daki 'new Comment(...)' satırı için constructor
    public Comment(String content, String authorName, int authorId, String authorRole, String announcementId) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.authorName = authorName;
        this.authorId = authorId;
        this.authorRole = authorRole;
        this.announcementId = announcementId;
        this.date = LocalDateTime.now();
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; } // Düzenleme için
    public String getAuthorName() { return authorName; }
    public String getAnnouncementId() { return announcementId; }
    public LocalDateTime getDate() { return date; }
    public String getAuthorRole() { return authorRole; } //
    public int getAuthorId() { return authorId; } //
}