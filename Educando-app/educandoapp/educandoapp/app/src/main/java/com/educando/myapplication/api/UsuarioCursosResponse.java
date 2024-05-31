package com.educando.myapplication.api;

import com.educando.myapplication.Course;
import com.educando.myapplication.Usuario;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UsuarioCursosResponse {
    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("cursos")
    private List<CursoUsuario> cursos;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<CursoUsuario> getCursos() {
        return cursos;
    }

    public void setCursos(List<CursoUsuario> cursos) {
        this.cursos = cursos;
    }

    public static class CursoUsuario {
        @SerializedName("id_mis_curso")
        private int idMisCurso;

        @SerializedName("id_curso")
        private int idCurso;

        @SerializedName("nombre_curso")
        private String nombreCurso;

        @SerializedName("descripcion_curso")
        private String descripcionCurso;

        @SerializedName("imagen_url")
        private String imagenUrl;

        public int getIdMisCurso() {
            return idMisCurso;
        }

        public void setIdMisCurso(int idMisCurso) {
            this.idMisCurso = idMisCurso;
        }

        public int getIdCurso() {
            return idCurso;
        }

        public void setIdCurso(int idCurso) {
            this.idCurso = idCurso;
        }

        public String getNombreCurso() {
            return nombreCurso;
        }

        public void setNombreCurso(String nombreCurso) {
            this.nombreCurso = nombreCurso;
        }

        public String getDescripcionCurso() {
            return descripcionCurso;
        }

        public void setDescripcionCurso(String descripcionCurso) {
            this.descripcionCurso = descripcionCurso;
        }

        public String getImagenUrl() {
            return imagenUrl;
        }

        public void setImagenUrl(int imagenUrl) {
            this.idMisCurso = imagenUrl;
        }
    }
}
