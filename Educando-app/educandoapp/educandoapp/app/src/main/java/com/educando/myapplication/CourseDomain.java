package com.educando.myapplication;

public class CourseDomain {
    private String title;
    private String owner;
    private double price;
    private String time;
    private String picPath;

    private String description;

    public CourseDomain(String title, String owner, double price, String time, String picPath) {
        this.title = title;
        this.owner = owner;
        this.price = price;
        this.time = time;
        this.picPath = picPath;
    }

    public CourseDomain() {
        // Constructor vacío
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTime() { // Cambié el nombre de getStar a getTime
        return time;
    }

    public void setTime(String time) { // Cambié el nombre de setStar a setTime
        this.time = time;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    // Getters y setters para manipular el atributo de descripción
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

