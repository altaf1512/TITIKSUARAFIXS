package com.arin.titik_suara.Model;

import java.io.Serializable;

public class PengaduanModel implements Serializable {
    private String idPengaduan;
    private String deskripsi;
    private String kategori;
    private String statusPengaduan;
    private String dibuatPada;
    private String foto;
    private String image;

    // Constructor
    public PengaduanModel(String idPengaduan, String deskripsi, String kategori,
                          String statusPengaduan, String dibuatPada, String foto, String foto1, String foto2, String image) {
        this.idPengaduan = idPengaduan;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.statusPengaduan = statusPengaduan;
        this.dibuatPada = dibuatPada;
        this.foto = foto;
        this.image = image;
    }

    // Getter dan Setter
    public String getIdPengaduan() {
        return idPengaduan;
    }

    public void setIdPengaduan(String idPengaduan) {
        this.idPengaduan = idPengaduan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getStatusPengaduan() {
        return statusPengaduan;
    }

    public void setStatusPengaduan(String statusPengaduan) {
        this.statusPengaduan = statusPengaduan;
    }

    public String getDibuatPada() {
        return dibuatPada;
    }

    public void setDibuatPada(String dibuatPada) {
        this.dibuatPada = dibuatPada;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "PengaduanModel{" +
                "idPengaduan='" + idPengaduan + '\'' +
                ", deskripsi='" + deskripsi + '\'' +
                ", kategori='" + kategori + '\'' +
                ", statusPengaduan='" + statusPengaduan + '\'' +
                ", dibuatPada='" + dibuatPada + '\'' +
                ", foto='" + foto + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}