package com.example.java_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Classroom implements Serializable {
    private static final long serialVersionUID = 1L;

    private String classId; // Öğrencinin gireceği kısa kod (Örn: "MATH101")
    private String className;
    private String teacherId; // Sınıfı oluşturan hocanın ID'si
    private List<Integer> studentIds; // Sınıfa katılan öğrencilerin/editörlerin listesi

    public Classroom(String className, String teacherId) {
        this.className = className;
        this.teacherId = teacherId;
        // Rastgele 6 haneli benzersiz bir kod üretelim
        this.classId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.studentIds = new ArrayList<>();
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
}
