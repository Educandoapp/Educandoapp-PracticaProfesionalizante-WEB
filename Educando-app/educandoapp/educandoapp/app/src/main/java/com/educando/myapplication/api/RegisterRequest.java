package com.educando.myapplication.api;

public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private int id_rol_id; // Nuevo campo para el id_rol_id

    public RegisterRequest(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.id_rol_id = 3; // Establecer el id_rol_id como 3 por defecto
    }

    // Agrega getters y setters si es necesario

    public int getIdRolId() {
        return id_rol_id;
    }

    public void setIdRolId(int idRolId) {
        this.id_rol_id = idRolId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
