package com.example.java_project;

public interface IAnnouncer {
    // Duyuru nesnesi alarak paylaşım yapar
    void postAnnouncement(Announcement announcement);

    // ID yerine nesnenin kendisini alarak silme yapar
    void deleteAnnouncement(Announcement announcement);
}
