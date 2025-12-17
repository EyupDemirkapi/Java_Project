package com.example.java_project;

public class Kullanıcı {
    private String Ad;
    private String Soyad;
    private int ID;
    private String Sifre;
    private String Bolum;
    public Kullanıcı(){}
    public Kullanıcı(String ad,String soyad,int id,String sifre,String bolum){
        this.Ad = ad;
        this.Soyad = soyad;
        this.ID = id;
        this.Sifre = sifre;
        this.Bolum = bolum;
    }
}
