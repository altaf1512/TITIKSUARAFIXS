package com.arin.titik_suara.Fragment;

public class PengaduanItem {
    private String id;
    private String deskripsi;
    private String kategori;
    private String dibuat_pada;
    private int status;

    public PengaduanItem(String id, String deskripsi, String kategori,
                         String dibuat_pada, int status) {
        this.id = id;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.dibuat_pada = dibuat_pada;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getDeskripsi() { return deskripsi; }
    public String getKategori() { return kategori; }
    public String getDibuat_pada() { return dibuat_pada; }
    public int getStatus() { return status; }

    // Get status text
    public String getStatusText() {
        switch(status) {
            case 1: return "Menunggu";
            case 2: return "Diproses";
            case 3: return "Selesai";
            default: return "Unknown";
        }
    }
}
