package com.example.amst_proyecto.data;

public class Usuario {
    private String nombre;
    private String photo;

    public Usuario() {
    }

    public Usuario(String nombre, String photo) {
        this.nombre = nombre;
        this.photo = photo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
