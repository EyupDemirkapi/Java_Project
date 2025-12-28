package com.example.java_project;

import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String email; // Sadece mail kısmı eklendi
    private int id;
    private String password;
    private String department;
    private long lastVisit;

    public User() {}

    public User(String name, String email, int id, String password, String department) {
        this.name = name;
        this.email = email; // Constructor'a mail eklendi
        this.id = id;
        setPassword(password);
        this.department = department;
    }

    // --- GETTER VE SETTER METODLARI ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Mail Getter/Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getID() { return id; }
    public void setID(int id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password != null && password.length() >= 6) {
            this.password = password;
        } else {
            this.password = "default123";
            System.out.println("Hata: Şifre yetersiz, varsayılan atandı.");
        }
    }

    public long getLastVisit() { return lastVisit; }
    public void setLastVisit(long lastVisit) { this.lastVisit = lastVisit; }

    @Override
    public String toString() {
        return "User{ID=" + id + ", Ad='" + name + "', Mail='" + email + "'}";
    }

    public String getRole() {
        if (this instanceof Teacher) return "Teacher";
        if (this instanceof Editor) return "Editor";
        return "Student";
    }
}