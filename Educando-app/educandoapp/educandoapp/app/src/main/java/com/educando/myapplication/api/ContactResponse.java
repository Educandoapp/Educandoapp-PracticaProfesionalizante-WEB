package com.educando.myapplication.api;

import java.util.Date;

public class ContactResponse {
    private String mensaje;
    private String titulo;
    private Date fecha_mensaje;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFecha() {
        return fecha_mensaje;
    }
}
