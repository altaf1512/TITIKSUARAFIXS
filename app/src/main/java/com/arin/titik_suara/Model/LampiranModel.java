package com.arin.titik_suara.Model;

import java.io.Serializable;

public class LampiranModel implements Serializable {
    private String idLampiran;
    private String foto;

    // Constructor
    public LampiranModel(String idLampiran, String foto) {
        this.idLampiran = idLampiran;
        this.foto = foto;
    }

    // Getter dan Setter
    public String getIdLampiran() {
        return idLampiran;
    }

    public void setIdLampiran(String idLampiran) {
        this.idLampiran = idLampiran;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "LampiranModel{" +
                "idLampiran='" + idLampiran + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}