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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.educando.myapplication.api.ApiManager;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        // Inicializar componentes de la UI
        nombre_main = findViewById(R.id.nombre_main);
        initRecyclerView();
        setupCategoryRecyclerView();

        // Agregar log para verificar si se inicializaron los componentes de la UI correctamente
        Log.d(TAG, "UI components initialized successfully");

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

        // Configuración del botón de abrir URL
        Button openUrlButton = findViewById(R.id.button_compra);
        openUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://10.0.2.2:4200";
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
        Call<Usuario> call = apiService.obtenerUsuario(tokenMap);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Obtener los detalles del usuario desde la respuesta
                    Usuario usuario = response.body();
                    String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
                    nombre_main.setText(nombreCompleto);
                } else {
                    // Manejar el error de respuesta
                    int statusCode = response.code();
                    Toast.makeText(MainActivity.this, "Error al obtener los detalles del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                // Manejar el error de la solicitud HTTP
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerView() {
        // recyclerViewCourse = findViewById(R.id.view);
        //recyclerViewCourse.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Agregar log para verificar la inicialización del RecyclerView de cursos
        Log.d(TAG, "RecyclerView de cursos inicializado");

        // Obtener los cursos desde la API
        // obtenerCursos();
    }

    private void obtenerCursos() {
        ApiService apiService = ApiManager.getService();
        // Obtener el token de autenticación de UserSession
        String authToken = "Bearer " + userSession.getAuthToken();

        // Imprimir el token antes de enviar la solicitud
        Log.d("Token", authToken);

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

                    // Generar una lista de índices aleatorios
                    List<Integer> randomIndices = generateRandomIndices(allCourses.size());
                    Log.d(TAG, "Índices aleatorios generados: " + randomIndices.toString());

                    // Seleccionar aleatoriamente un subconjunto de cursos
                    List<Course> randomCourses = new ArrayList<>();
                    for (int index : randomIndices) {
                        randomCourses.add(allCourses.get(index));
                    }

                    // Convertir la lista de cursos a ArrayList<CourseDomain>
                    ArrayList<CourseDomain> courseDomains = new ArrayList<>();
                    for (Course course : randomCourses) {
                        CourseDomain courseDomain = new CourseDomain();
                        courseDomain.setTitle(course.getName());
                        courseDomain.setDescription(course.getDescription());
                        // Agregar el CourseDomain a la lista
                        courseDomains.add(courseDomain);
                    }
                    // Inicializar el adaptador con la lista de cursos convertida
                    adpterCourses = new CourseAdapter(courseDomains);
                    recyclerViewCourse.setAdapter(adpterCourses);

                    // Agregar log al inicializar el adaptador
                    Log.d(TAG, "Adaptador de cursos inicializado con " + courseDomains.size() + " cursos");

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
    private List<Integer> generateRandomIndices(int size) {
        List<Integer> randomIndices = new ArrayList<>();
        Random random = new Random();
        while (randomIndices.size() < 6) { // Define NUM_COURSES_TO_DISPLAY como el número de cursos que deseas mostrar
            int randomIndex = random.nextInt(size);
            if (!randomIndices.contains(randomIndex)) {
                randomIndices.add(randomIndex);
            }
        }

        // Agregar log para verificar los índices generados
        Log.d(TAG, "Índices aleatorios generados: " + randomIndices.toString());

        return randomIndices;
    }

    private void setupCategoryRecyclerView() {
        // recyclerViewCategory = findViewById(R.id.recycler_view_categories);
        // recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Agregar log para verificar la inicialización del RecyclerView de categorías
        Log.d(TAG, "RecyclerView de categorías inicializado");

        // Obtener las categorías desde la API
        // obtenerCategorias();
    }

    private void obtenerCategorias() {
        ApiService apiService = ApiManager.getService();
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Agregar log al recibir una respuesta exitosa
                    Log.d(TAG, "Respuesta exitosa al obtener categorías");

                    // Actualizar RecyclerView con la lista de categorías obtenida
                    List<Category> categoryList = response.body();
                    for (Category category : categoryList) {
                        Log.d(TAG, "Categoría: " + category.getName());
                    }

                    List<CategoryDomain> categoryDomainList = convertToCategoryDomainList(categoryList);
                    categoryAdapter = new CategoryAdapter(new ArrayList<>(categoryDomainList));
                    recyclerViewCategory.setAdapter(categoryAdapter);

                    // Agregar log al inicializar el adaptador
                    Log.d(TAG, "Adaptador de categorías inicializado con " + categoryDomainList.size() + " categorías");

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

    private List<CategoryDomain> convertToCategoryDomainList(List<Category> categoryList) {
        List<CategoryDomain> categoryDomainList = new ArrayList<>();
        for (Category category : categoryList) {
            // Aquí deberías obtener la referencia de la imagen de la categoría
            // Puedes obtenerla de la respuesta del backend o de otro lugar según tu implementación
            String imageResource = ""; // Aquí establece la referencia de la imagen según la lógica de tu aplicación
            CategoryDomain categoryDomain = new CategoryDomain(category.getName(), imageResource);
            categoryDomainList.add(categoryDomain);
        }

        // Agregar log para verificar la conversión de categorías
        Log.d(TAG, "Convertidos " + categoryDomainList.size() + " categorías a CategoryDomain");

        return categoryDomainList;
    }
}
