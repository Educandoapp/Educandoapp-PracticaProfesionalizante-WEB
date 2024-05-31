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
import retrofit2.http.DELETE;
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

    @POST("registro/")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

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

    @POST("recordatorios/")
    Call<Recordatorio> guardarRecordatorio(@Body Recordatorio recordatorio);

    @POST("recordatorios/eliminar/{id_recordatorio}/")
    Call<Void> eliminarRecordatorio(@Path("id_recordatorio") int id_recordatorio);

    @GET("cursos_favoritos/")
    Call<List<Course>> getCursosFavoritos();

    @POST("cursos_favoritos/")
    Call<Void> guardarCursoFavorito(@Body CursoFavoritoRequest request);

    @POST("cursos_favoritos/{id_curso_favorito}/")
    Call<Void> eliminarCursoFavorito(@Path("id_curso_favorito") int id_curso_favorito);

}
