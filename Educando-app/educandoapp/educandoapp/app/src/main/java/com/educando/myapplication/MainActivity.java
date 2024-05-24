package com.educando.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.educando.myapplication.db.DbHelper;
import com.educando.myapplication.db.DbUsuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adpterCourses;
    private RecyclerView.Adapter categoryAdapter;
    private TextView nombre_main;
    public RecyclerView recyclerViewCategory;
    public RecyclerView recyclerViewCourse;
    private DbHelper dbHelper;
    private DbUsuarios dbUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);
        dbUsuarios = new DbUsuarios(this); // Inicializa DbUsuarios

        initRecyclerView();
        setupCategoryRecyclerView();

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

        // Obtén una referencia al TextView nombre_main
        nombre_main = findViewById(R.id.nombre_main);

        LinearLayout cuenta = findViewById(R.id.Cuenta);
        LinearLayout miscursos = findViewById(R.id.Mycourse);

        // Aquí obtén el nombre del usuario logueado, por ejemplo, de tu sistema de autenticación
        Usuario nombreUsuario = dbUsuarios.obtenerUsuarioLogueado();

        if (nombreUsuario != null) {
            // Establece el nombre del usuario en el TextView
            String nombreCompleto = nombreUsuario.getNombre() + " " + nombreUsuario.getApellido();
            nombre_main.setText(nombreCompleto);
        }

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
    }

    private void initRecyclerView() {
        recyclerViewCourse = findViewById(R.id.view);
        recyclerViewCourse.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Obtener los cursos desde la base de datos
        ArrayList<CourseDomain> courseList = (ArrayList<CourseDomain>) dbHelper.getAllCourses(this);

        // Inicializar el adaptador con la lista de cursos
        adpterCourses = new CourseAdapter(courseList);
        recyclerViewCourse.setAdapter(adpterCourses);
    }

    private void setupCategoryRecyclerView() {
        recyclerViewCategory = findViewById(R.id.recycler_view_categories);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Obtener las categorías desde el array
        ArrayList<CategoryDomain> categoryList = (ArrayList<CategoryDomain>) dbHelper.getAllCategories(this);

        // Inicializar el adaptador con el array de categorías y el contexto
        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryList);
        recyclerViewCategory.setAdapter(categoryAdapter);
    }
}
