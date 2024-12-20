package com.arin.titik_suara.Model;

public class UserModel {
    private String id_user;
    private String nama;
    private String jabatan;
    private String alamat;
    private String no_telepon;
    private String foto_profil;

    // Constructor
    public UserModel(String id_user, String nama, String jabatan, String alamat, String no_telepon, String foto_profil) {
        this.id_user = id_user;
        this.nama = nama;
        this.jabatan = jabatan;
        this.alamat = alamat;
        this.no_telepon = no_telepon;
        this.foto_profil = foto_profil;
    }

    // Default Constructor
    public UserModel() {
    }

    // Getter dan Setter
    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNo_telepon() {
        return no_telepon;
    }

    public void setNo_telepon(String no_telepon) {
        this.no_telepon = no_telepon;
    }

    public String getFoto_profil() {
        return foto_profil;
    }

    public void setFoto_profil(String foto_profil) {
        this.foto_profil = foto_profil;
    }
}