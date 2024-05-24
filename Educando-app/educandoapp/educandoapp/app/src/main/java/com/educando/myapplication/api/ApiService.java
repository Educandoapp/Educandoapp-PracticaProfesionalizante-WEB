package com.educando.myapplication.api;

import com.educando.myapplication.Course;
import com.educando.myapplication.Usuario;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // Define los métodos para las operaciones de tu API

    // ApiService.java
    @GET("test_connection/")
    Call<JsonObject> testConnection();

    @POST("login/")
    Call<Usuario> iniciarSesion(@Body Usuario usuario);

    @GET("usuario/{id_usuario}/")
    Call<Usuario> obtenerUsuarioPorId(@Path("id_usuario") int idUsuario);

    @POST("auth/validar_password/")
    Call<Usuario> validarPassword(@Body Usuario usuario);

    // @POST("crear_usuario/")
    // Call<Void> crearUsuario(@Body Usuario usuario);

    // @PUT("actualizar_usuario/{id_usuario}/")
    // Call<Void> actualizarUsuario(@Path("id_usuario") int idUsuario, @Body Usuario usuario);

    @GET("courses/") // El endpoint de la API para obtener la lista de cursos
    Call<List<Course>> getCourses();
}
