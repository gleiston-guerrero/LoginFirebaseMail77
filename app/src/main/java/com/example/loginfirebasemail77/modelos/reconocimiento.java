package com.example.loginfirebasemail77.modelos;

public class reconocimiento {
    private String idReconocimiento;
    private String imagen;
    private String macDispositivo;

    public reconocimiento() {
    }

    public reconocimiento(String idReconocimiento, String imagen, String macDispositivo) {
        this.idReconocimiento = idReconocimiento;
        this.imagen = imagen;
        this.macDispositivo = macDispositivo;
    }

    public String getIdReconocimiento() {
        return idReconocimiento;
    }

    public void setIdReconocimiento(String idReconocimiento) {
        this.idReconocimiento = idReconocimiento;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getMacDispositivo() {
        return macDispositivo;
    }

    public void setMacDispositivo(String macDispositivo) {
        this.macDispositivo = macDispositivo;
    }
}
