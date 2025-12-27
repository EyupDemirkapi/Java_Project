package com.example.java_project;

import java.io.Serializable;

public abstract class User implements Serializable {
    // Versiyon kontrolü: Dosyadan okurken hata almamak için şart
    private static final long serialVersionUID = 1L;

    // Property yerine düz veri tipleri kullanıyoruz (Serileştirilebilir olması için)
    private String name;
    private String surname;
    private int id;
    private String password;
    private String department;
    private long lastVisit;

    public User() {}

    public User(String name, String surname, int id, String password, String department) {
        this.name = name;
        this.surname = surname;
        this.id = id;
        setPassword(password); // Kontrol mekanizması için setter kullandık
        this.department = department;
    }

    // --- GETTER VE SETTER METODLARI ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public int getID() { return id; }
    public void setID(int id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password != null && password.length() >= 6) {
            this.password = password;
        } else {
            // Varsayılan bir şifre atayabilir veya hata fırlatabilirsin
            this.password = "default123";
            System.out.println("Hata: Şifre yetersiz, varsayılan atandı.");
        }
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public long getLastVisit() { return lastVisit; }
    public void setLastVisit(long lastVisit) { this.lastVisit = lastVisit; }

    // --- ORTAK METODLAR ---

    public void makeComment(String comment) {
        System.out.println(getName() + " yorum yaptı: " + comment);
    }
    public void deleteComment(Comment comment) {
        DataStore.comments.remove(comment);
        DataStore.saveAll(); // Dosyayı güncelle
        System.out.println(getName() + " isimli kullanıcı bir yorum sildi.");
    }
    @Override
    public String toString() {
        return "User{ID=" + id + ", Ad='" + name + "', Departman='" + department + "'}";
    }
    public String getRole() {
        if (this instanceof Teacher) return "Teacher";
        if (this instanceof Editor) return "Editor";
        return "Student";
    }
}