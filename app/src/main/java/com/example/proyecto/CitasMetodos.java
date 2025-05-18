package com.example.proyecto;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

public class CitasMetodos {
    private int idCita;
    private String usuario;
    private String ciudad;
    private String tipoCita;
    private String fecha;
    private String hora;
    private int precio;
    private String personal;

    public CitasMetodos(){

    }

    public CitasMetodos(int idCita, String usuario, String ciudad, String tipoCita, String fecha, String hora, int precio, String personal) {
        this.idCita=idCita;
        this.usuario = usuario;
        this.ciudad = ciudad;
        this.tipoCita = tipoCita;
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.personal = personal;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getTipoCita() {
        return tipoCita;
    }

    public void setTipoCita(String tipoCita) {
        this.tipoCita = tipoCita;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    @Override
    public String toString() {
        return "CitasMetodos{" +
                "idCita=" + idCita +
                ", usuario='" + usuario + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", tipoCita='" + tipoCita + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", precio=" + precio +
                ", personal='" + personal + '\'' +
                '}';
    }
}
