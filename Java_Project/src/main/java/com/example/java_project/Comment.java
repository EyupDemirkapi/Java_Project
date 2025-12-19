package com.example.java_project;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String content;
    private String authorName;
    private String announcementId; // Hangi duyuruya yapıldığını bilmek için
    private LocalDateTime date;

    public Comment(String content, String authorName, String announcementId) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.authorName = authorName;
        this.announcementId = announcementId;
        this.date = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
}