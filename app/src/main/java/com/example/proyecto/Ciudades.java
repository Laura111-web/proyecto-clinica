package com.example.proyecto;

public class Ciudades {

    private String ciudad;
    private String calle;
    private int telefono;

    public Ciudades() {

    }

    public Ciudades(String ciudad, String calle, int telefono) {
        this.ciudad = ciudad;
        this.calle = calle;
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Ciudades{" +
                "ciudad='" + ciudad + '\'' +
                ", calle='" + calle + '\'' +
                ", telefono=" + telefono +
                '}';
    }
}
