package com.educando.myapplication.api;

import com.google.gson.annotations.SerializedName;

public class CursoFavoritoRequest {
    @SerializedName("id_curso")
    private int id_curso;

    @SerializedName("id_usuario")
    private int id_usuario;

    public CursoFavoritoRequest(int id_curso, int id_usuario) {
        this.id_curso = id_curso;
        this.id_usuario = id_usuario;
    }
}
