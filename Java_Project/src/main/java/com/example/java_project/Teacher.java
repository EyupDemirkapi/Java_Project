package com.example.java_project;

public class Teacher extends User implements IAnnouncer {
    // Serileştirme hatası almamak için düz String kullanıyoruz
    private String expertise;

    public Teacher() {
        super();
    }

    public Teacher(String name, String surname, int id, String password, String department, String expertise) {
        super(name, surname, id, password, department);
        this.expertise = expertise;
    }

    // --- GETTER VE SETTER ---
    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }

    @Override
    public String toString() {
        return super.toString() + " [Hoca Uzmanlık: " + expertise + "]";
    }

    @Override
    public void postAnnouncement(Announcement announcement) {
        // Konsol çıktısı yerine gerçek işlev: Veriyi DataStore'a ekle
        DataStore.addAnnouncement(announcement);
        System.out.println("Hoca " + getName() + " duyuruyu paylaştı.");
    }

    @Override
    public void deleteAnnouncement(Announcement announcement) {
        // Konsol çıktısı yerine gerçek işlev: Veriyi DataStore'dan sil
        DataStore.deleteAnnouncement(announcement);
        System.out.println("Hoca " + getName() + " duyuruyu sildi.");
    }
}