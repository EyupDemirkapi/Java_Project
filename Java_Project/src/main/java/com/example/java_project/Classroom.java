package com.example.java_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.java_project.DataStore.announcements;

public class Classroom implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Announcement> announcements = new ArrayList<>();
    private String classId; // Öğrencinin gireceği kısa kod (Örn: "MATH101")
    private String className;
    private String teacherId; // Sınıfı oluşturan hocanın ID'si
    private List<Integer> studentIds; // Sınıfa katılan öğrencilerin/editörlerin listesi
    private List<Comment> comments = new ArrayList<>();
    public Classroom(String className, String teacherId) {
        this.className = className;
        this.teacherId = teacherId;
        // Rastgele 6 haneli benzersiz bir kod üretelim
        this.classId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.studentIds = new ArrayList<>();
    }
    public void addAnnouncement(Announcement ann) {
        if (announcements == null) {
            announcements = new ArrayList<>();
        }
        this.announcements.add(ann);
    }
    public List<Announcement> getAnnouncements() {
        if (announcements == null) {
            announcements = new ArrayList<>();
        }
        return announcements;
    }
    // Getters
    public String getClassId() { return classId; }
    public String getClassName() { return className; }
    public String getTeacherId() { return teacherId; }
    public List<Integer> getStudentIds() { return studentIds; }


    public void addStudent(int studentId) {
        if (!studentIds.contains(studentId)) {
            studentIds.add(studentId);
        }
    }


    public List<Comment> getComments() {
        return comments; // chatController refreshChat() için
    }

    public void addComment(Comment comment) {
        this.comments.add(comment); // chatController handleSendMessage() için
    }
}
