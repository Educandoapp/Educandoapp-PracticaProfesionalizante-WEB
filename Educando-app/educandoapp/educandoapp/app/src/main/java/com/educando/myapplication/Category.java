package com.educando.myapplication;

public class Category {
    private int id_categoria;
    private String nombre;
    private String categoria_imagen_url;

    public Category() {
        // Constructor vacío por defecto
    }

    public Category(int id_categoria, String nombre, String categoria_imagen_url) {
        this.id_categoria = id_categoria;
        this.nombre = nombre;
        this.categoria_imagen_url = categoria_imagen_url;
    }

    public Category(String nombre) {
        this.nombre = nombre;
    }

    public Category(String nombre, String categoria_imagen_url) {
        this.nombre = nombre;
        this.categoria_imagen_url = categoria_imagen_url;
    }

    public int getId() {
        return id_categoria;
    }

    public void setId(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getName() {
        return nombre;
    }

    public void setName(String nombre) {
        this.nombre = nombre;
    }

    public String getImageUrl() {
        return categoria_imagen_url;
    }

    public void setImageUrl(String categoria_imagen_url) {
        this.categoria_imagen_url = categoria_imagen_url;
    }
}
