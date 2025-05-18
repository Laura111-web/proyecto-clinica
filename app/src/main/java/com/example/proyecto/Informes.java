package com.example.proyecto;

import java.util.Date;

public class Informes {

    private int idInforme;
    private String usuario;
    private Date fecha;
    private String archivo;

    public Informes(){

    }

    public Informes(int idInforme, String usuario, Date fecha, String archivo) {
        this.idInforme = idInforme;
        this.usuario = usuario;
        this.fecha = fecha;
        this.archivo = archivo;
    }

    public int getIdInforme() {
        return idInforme;
    }

    public void setIdInforme(int idInforme) {
        this.idInforme = idInforme;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    @Override
    public String toString() {
        return "Informes{" +
                "idInforme=" + idInforme +
                ", usuario='" + usuario + '\'' +
                ", fecha=" + fecha +
                ", archivo='" + archivo + '\'' +
                '}';
    }
}
