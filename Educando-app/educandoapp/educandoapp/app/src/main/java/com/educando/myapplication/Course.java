package com.educando.myapplication;

import com.google.gson.annotations.SerializedName;

public class Course {

    @SerializedName("id_curso")
    private int id_curso;
    @SerializedName("nombre_curso")
    private String nombre_curso;
    @SerializedName("descripcion")
    private String descripcion;
    private int duracion;
    private int precio;
    private float calificacion;
    private String imagen_url;
    @SerializedName("favorito")
    private boolean favorito;

    // Constructor vacío
    public Course() {
    }

    // Constructor con parámetros
    public Course(int id_curso, String nombre_curso, String descripcion, boolean favorito) {
        this.id_curso = id_curso;
        this.nombre_curso = nombre_curso;
        this.descripcion = descripcion;
        this.favorito = favorito;
    }

    public Course(String nombre_curso, String descripcion, int duracion, int precio, float calificacion, String imagen_url) {
        this.nombre_curso = nombre_curso;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.precio = precio;
        this.calificacion = calificacion;
        this.imagen_url = imagen_url;
    }

    public int getId() {
        return id_curso;
    }

    public void setId(int id_curso) {
        this.id_curso = id_curso;
    }

    public String getName() {
        return nombre_curso;
    }

    public void setName(String nombre_curso) {
        this.nombre_curso = nombre_curso;
    }

    public String getDescription() {
        return descripcion;
    }

    public void setDescription(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDuration() {
        return duracion;
    }

    public void setDuration(int duracion) {
        this.duracion = duracion;
    }

    public int getPrice() {
        return precio;
    }

    public void setPrice(int precio) {
        this.precio = precio;
    }

    public float getRating() {
        return calificacion;
    }

    public void setRating(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getImageUrl() {
        return imagen_url;
    }

    public void setImageUrl(String imagen_url) {
        this.imagen_url = imagen_url;
    }

    public boolean isFavorite() {
        return favorito;
    }

    public void setFavorite(boolean favorito) {
        this.favorito = favorito;
    }
}
