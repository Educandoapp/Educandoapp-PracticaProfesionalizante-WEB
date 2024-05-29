package com.educando.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.educando.myapplication.api.ApiManager;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.UserDetailsResponse;
import com.educando.myapplication.api.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView.Adapter adpterCourses;
    private RecyclerView.Adapter categoryAdapter;
    private TextView nombre_main;
    public RecyclerView recyclerViewCategory;
    public RecyclerView recyclerViewCourse;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userSession = UserSession.getInstance(this); // Obtener la instancia de UserSession

        // Manejar clic en icono de Instagram
        ImageView instagramIcon = findViewById(R.id.instagram_icon);
        instagramIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String instagramUrl = "https://www.instagram.com/tu_pagina_de_instagram";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl));
                startActivity(intent);
            }
        });

        // Manejar clic en icono de Facebook
        ImageView facebookIcon = findViewById(R.id.facebook_icon);
        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebookUrl = "https://www.facebook.com/tu_pagina_de_facebook";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
                startActivity(intent);
            }
        });

        // Inicializar componentes de la UI
        nombre_main = findViewById(R.id.nombre_main);
        initRecyclerView();
        setupCategoryRecyclerView();

        // Agregar log para verificar si se inicializaron los componentes de la UI correctamente
        Log.d(TAG, "UI components initialized successfully");

        obtenerCategorias();

        //referencia al boton del mapa
        LinearLayout layoutUbicacion = findViewById(R.id.map);
        layoutUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia la actividad del mapa
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        // Obteniendo los detalles del usuario desde el backend
        obtenerDetallesUsuario();

        // Configuración de clics para las opciones de cuenta y cursos
        LinearLayout cuenta = findViewById(R.id.Cuenta);
        LinearLayout miscursos = findViewById(R.id.Mycourse);

        cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        miscursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyCourseActivity.class);
                startActivity(intent);
            }
        });

        // Configuración del botón de WhatsApp
        FloatingActionButton fabWhatsapp = findViewById(R.id.fab_whatsapp);
        fabWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "+5493876286703";
                String url = "https://wa.me/" + phoneNumber;
                url = url + "?text=" + "Hola, quiero información sobre los cursos";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "No se puede abrir WhatsApp. Instala la aplicación.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Obtén una referencia al botón "Abrir URL"
        Button openUrlButton = findViewById(R.id.button_compra);

        // Configura un oyente de clic para el botón
        openUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define la URL que deseas abrir
                String url = "http://10.0.2.2:4200"; // Reemplaza con tu URL real

                // Crea un intent para abrir la URL en un navegador web
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

            }
        });
    }

    private void obtenerDetallesUsuario() {
        // Crear una instancia del servicio API
        ApiService apiService = ApiManager.getService();

        // Obtener el token de autenticación de UserSession
        String authToken = userSession.getAuthToken();

        // Crear un mapa para enviar el token en el cuerpo de la solicitud
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", authToken);

        // Realizar la solicitud POST para obtener los detalles del usuario
        Call<UserDetailsResponse> call = apiService.obtenerUsuario(tokenMap);
        call.enqueue(new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Obtener los detalles del usuario desde la respuesta
                    UserDetailsResponse userDetails = response.body();
                    String nombreCompleto = userDetails.getNombre() + " " + userDetails.getApellido();
                    nombre_main.setText(nombreCompleto);

                    // Guardar los detalles del usuario en SharedPreferences
                    userSession.setUserSession(userDetails.getEmail(), userDetails.getNombre(), userDetails.getApellido(), authToken, userDetails.getId());
                } else {
                    // Manejar el error de respuesta
                    int statusCode = response.code();
                    Toast.makeText(MainActivity.this, "Error al obtener los detalles del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                // Manejar el error de la solicitud HTTP
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerView() {
        recyclerViewCourse = findViewById(R.id.view);
        recyclerViewCourse.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Agregar log para verificar la inicialización del RecyclerView de cursos
        Log.d(TAG, "RecyclerView de cursos inicializado");

        // Obtener los cursos desde la API
        obtenerCursos();
    }

    private void obtenerCursos() {
        ApiService apiService = ApiManager.getService();

        Call<List<Course>> call = apiService.getCourses();
        call.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Agregar log al recibir una respuesta exitosa
                    Log.d(TAG, "Respuesta exitosa al obtener cursos");

                    // Obtener la lista de todos los cursos
                    List<Course> allCourses = response.body();
                    for (Course course : allCourses) {
                        Log.d(TAG, "Curso: " + course.getName() + ", Descripción: " + course.getDescription());
                    }

                    // Verificar si hay suficientes cursos para seleccionar 6 aleatoriamente
                    if (allCourses.size() >= 6) {
                        // Generar una lista de índices aleatorios únicos
                        List<Integer> randomIndices = generateRandomIndices(allCourses.size(), 6);
                        Log.d(TAG, "Índices aleatorios generados: " + randomIndices.toString());

                        // Seleccionar aleatoriamente 6 cursos
                        List<Course> randomCourses = new ArrayList<>();
                        for (int index : randomIndices) {
                            randomCourses.add(allCourses.get(index));
                        }

                        // Inicializar el adaptador con la lista de cursos aleatorios
                        CourseAdapter adapter = new CourseAdapter(randomCourses);
                        recyclerViewCourse.setAdapter(adapter);

                        // Agregar log al inicializar el adaptador
                        Log.d(TAG, "Adaptador de cursos inicializado con " + randomCourses.size() + " cursos");
                    } else {
                        // No hay suficientes cursos para seleccionar 6 aleatoriamente
                        Toast.makeText(MainActivity.this, "No hay suficientes cursos disponibles", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    // Manejar errores de respuesta
                    Log.e(TAG, "Error al obtener la lista de cursos, código de respuesta: " + response.code());

                    // Manejar errores de respuesta
                    Toast.makeText(MainActivity.this, "Error al obtener la lista de cursos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {

                // Manejar errores de solicitud HTTP
                Log.e(TAG, "Error de conexión al obtener cursos: " + t.getMessage());

                // Manejar errores de solicitud HTTP
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para generar una lista de índices aleatorios
    private List<Integer> generateRandomIndices(int maxIndex, int count) {
        List<Integer> indices = new ArrayList<>();
        Random random = new Random();

        // Generar 'count' índices aleatorios únicos
        while (indices.size() < count) {
            int index = random.nextInt(maxIndex);
            if (!indices.contains(index)) {
                indices.add(index);
            }
        }

        return indices;
    }

    private void setupCategoryRecyclerView() {
        recyclerViewCategory = findViewById(R.id.recycler_view_categories);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Agregar log para verificar la inicialización del RecyclerView de categorías
        Log.d(TAG, "RecyclerView de categorías inicializado");

        // Obtener las categorías desde la API
        //obtenerCategorias();
    }

    private void obtenerCategorias() {
        ApiService apiService = ApiManager.getService();
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Log para imprimir la respuesta completa del servidor
                    Log.d(TAG, "Respuesta completa del servidor: " + response.toString());

                    // Convertir la respuesta del servidor a JSON (cadena de texto)
                    String jsonResponse = new Gson().toJson(response.body());
                    Log.d(TAG, "Respuesta JSON del servidor: " + jsonResponse);


                    // Agregar log al recibir una respuesta exitosa
                    Log.d(TAG, "Respuesta exitosa al obtener categorías");

                    // Actualizar RecyclerView con la lista de categorías obtenida
                    List<Category> categoryList = response.body();
                    for (Category category : categoryList) {
                        Log.d(TAG, "Categoría: " + category.getName());
                    }

                    // Inicializar el adaptador con la lista de categorías directamente
                    categoryAdapter = new CategoryAdapter(categoryList);
                    recyclerViewCategory.setAdapter(categoryAdapter);

                    // Agregar log al inicializar el adaptador
                    Log.d(TAG, "Adaptador de categorías inicializado con " + categoryList.size() + " categorías");

                } else {

                    // Manejar errores de respuesta
                    Log.e(TAG, "Error al obtener la lista de categorías, código de respuesta: " + response.code());

                    // Manejar errores de respuesta
                    Toast.makeText(MainActivity.this, "Error al obtener la lista de categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

                // Manejar errores de solicitud HTTP
                Log.e(TAG, "Error de conexión al obtener categorías: " + t.getMessage());

                // Manejar errores de solicitud HTTP
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
