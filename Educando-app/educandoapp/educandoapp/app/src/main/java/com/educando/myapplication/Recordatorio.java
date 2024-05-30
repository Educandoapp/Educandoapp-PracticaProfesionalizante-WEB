package com.educando.myapplication;

import com.google.gson.annotations.SerializedName;

public class Recordatorio {

    @SerializedName("id_recordatorio")
    private int id_recordatorio;

    @SerializedName("tarea")
    private String tarea;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("usuario")
    private int id_usuario;

    // Getters y setters
    public int getId() {
        return id_recordatorio;
    }

    public void setId(int id_recordatorio) {
        this.id_recordatorio = id_recordatorio;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return id_usuario;
    }

    public void setIdUsuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

}
