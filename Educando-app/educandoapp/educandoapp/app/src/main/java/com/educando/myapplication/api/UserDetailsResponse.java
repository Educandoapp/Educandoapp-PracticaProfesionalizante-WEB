package com.educando.myapplication.api;

import com.google.gson.annotations.SerializedName;

public class UserDetailsResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("email")
    private String email;

    // Otros campos según la estructura de la respuesta del servidor

    public UserDetailsResponse(int id, String nombre, String apellido, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Agrega getters y setters según sea necesario para acceder a los campos
}
