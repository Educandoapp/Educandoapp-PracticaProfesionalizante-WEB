package com.educando.myapplication.api;

import com.educando.myapplication.Category;
import com.educando.myapplication.Course;
import com.educando.myapplication.Recordatorio;
import com.educando.myapplication.Usuario;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Define los métodos para las operaciones de tu API

    // ApiService.java
    @GET("test_connection/")
    Call<JsonObject> testConnection();

    @POST("login/")
    Call<Usuario> iniciarSesion(@Body Usuario usuario);

    //@GET("usuario/{id_usuario}/")
    // Call<Usuario> obtenerUsuarioPorId(@Path("id_usuario") int idUsuario);

    // @POST("auth/validar_password/")
    //Call<Usuario> validarPassword(@Body Usuario usuario);

    @POST("registro/")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    // Nuevo método para obtener detalles del usuario
    @POST("obtener-usuario/")
    Call<UserDetailsResponse> obtenerUsuario(@Body Map<String, String> token);

    @PUT("usuarios/{id}/")
    Call<UserDetailsResponse> updatePassword(@Path("id") int id, @Body JsonObject payload);

    @GET("cursos_por_categoria/{id_categoria}/")
    Call<List<Course>> getCoursesByCategory(@Path("id_categoria") int idCategoria);

    @POST("mis_cursos/")
    Call<List<UsuarioCursosResponse.CursoUsuario>> obtenerUsuarioCursos(@Body Map<String, String> token);

    @GET("categorias/")
    Call<List<Category>> getCategories();

    @GET("cursos/")
    Call<List<Course>> getCourses();

    @POST("contacto/")
    Call<ContactResponse> enviarContacto(@Header("Authorization") String token, @Body ContactRequest contactRequest);

    @GET("contactos/")
    Call<List<ContactResponse>> obtenerContactos(@Query("email") String email);

    @GET("recordatorios/{id_usuario}/")
    Call<List<Recordatorio>> getRecordatoriosPorUsuario(@Path("id_usuario") int idUsuario);
}
