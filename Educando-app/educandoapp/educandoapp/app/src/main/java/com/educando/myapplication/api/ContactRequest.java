package com.educando.myapplication.api;

public class ContactRequest {
    private String email;
    private String nombre;
    private String titulo;
    private String mensaje;

    public ContactRequest(String email, String nombre, String titulo, String mensaje) {
        this.email = email;
        this.nombre = nombre;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}