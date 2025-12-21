package com.example.java_project;

public class Editor extends User implements IAnnouncer {
    private String expertise;

    public Editor() {
        super();
    }

    public Editor(String name, String surname, int id, String password, String department, String expertise) {
        super(name, surname, id, password, department); //
        this.expertise = expertise;
    }

    // --- GETTER VE SETTER ---
    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }

    @Override
    public void postAnnouncement(Announcement announcement) {
        // Duyuruyu merkezi veri deposuna ekle
        DataStore.addAnnouncement(announcement);
        System.out.println("Editör " + getName() + " yeni bir duyuru yayınladı.");
    }

    @Override
    public void deleteAnnouncement(Announcement announcement) {
        // Duyuruyu merkezi veri deposundan sil
        DataStore.deleteAnnouncement(announcement);
        System.out.println("Editör " + getName() + " duyuruyu sildi: " + announcement.getTitle());
    }

    @Override
    public String toString() {
        return super.toString() + " [Uzmanlık: " + expertise + "]";
    }
}