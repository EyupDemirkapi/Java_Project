package com.example.java_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Classroom implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Announcement> announcements = new ArrayList<>();
    private String classId;
    private String className;
    private String teacherId;
    private List<Integer> studentIds = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    public Classroom(String className, String teacherId) {
        this.className = className;
        this.teacherId = teacherId;
        this.classId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    // --- YORUM İŞLEMLERİ (Aynı Class İçinde) ---

    public void addComment(Comment comment) {
        if (this.comments == null) this.comments = new ArrayList<>();
        this.comments.add(comment);
        // Değişiklikten sonra merkezi kaydı tetikle
        DataStore.saveAll();
    }

    /**
     * Yorum silme işlemi.
     * @param comment Silinecek yorum
     * @param user Silmeye çalışan kullanıcı (Yetki kontrolü için)
     */
    public void deleteComment(Comment comment, User user) {
        // Yetki: Kullanıcı kendi yorumuysa veya Editörse silebilir
        if (user.getRole().equals("Editor") || comment.getAuthorId() == user.getID()) {
            this.comments.remove(comment);
            DataStore.saveAll();
            System.out.println("Sınıf " + classId + ": Bir yorum silindi.");
        }
    }

    // --- GETTERS & SETTERS ---
    public List<Comment> getComments() {
        if (comments == null) comments = new ArrayList<>();
        return comments;
    }

    public void addAnnouncement(Announcement ann) {
        if (announcements == null) announcements = new ArrayList<>();
        this.announcements.add(ann);
        DataStore.saveAll();
    }

    public List<Announcement> getAnnouncements() {
        if (announcements == null) announcements = new ArrayList<>();
        return announcements;
    }

    public String getClassId() { return classId; }
    public String getClassName() { return className; }
    public String getTeacherId() { return teacherId; }
    public List<Integer> getStudentIds() { return studentIds; }

    public void addStudent(int studentId) {
        if (!studentIds.contains(studentId)) {
            studentIds.add(studentId);
            DataStore.saveAll();
        }
    }
}