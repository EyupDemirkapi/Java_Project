package com.example.java_project;

public class Student extends User {
    // StringProperty yerine düz String kullanıyoruz (Dosyaya hatasız kayıt için)
    private String academicYear;

    public Student() {
        super();
    }

    public Student(String name, String surname, int id, String password, String department, String academicYear) {
        super(name, surname, id, password, department);
        this.academicYear = academicYear;
    }

    public String getAcademicYear() {
        return academicYear != null ? academicYear : "Belirtilmemiş";
    }

    // Setter
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    @Override
    public String toString() {
        return super.toString() + " [Sınıf: " + academicYear + "]";
    }
}