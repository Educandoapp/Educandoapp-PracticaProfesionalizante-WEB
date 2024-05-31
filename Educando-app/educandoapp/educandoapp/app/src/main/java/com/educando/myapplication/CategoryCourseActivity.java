package com.educando.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.bumptech.glide.Glide;
import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryCourseActivity extends AppCompatActivity {
    private static final String TAG = "CategoryCourseActivity";
    RecyclerView recyclerView;
    List<Course> courseList;
    CourseAdapter courseAdapter;
    String categoryName; // El nombre de la categoría seleccionada
    DecimalFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cour_of_cat);

        int categoryId = getIntent().getIntExtra("id_categoria", -1);

        if (categoryId == -1) {
            Toast.makeText(this, "ID de categoría no válido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String categoryName = getIntent().getStringExtra("nombre");

        TextView categoryNameTextView = findViewById(R.id.list_course);
        categoryNameTextView.setText(categoryName);

        recyclerView = findViewById(R.id.recycler_view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList, true);
        recyclerView.setAdapter(courseAdapter);

        Log.d(TAG, "RecyclerView and adapter configured");

        getCoursesByCategory(categoryId);

        LinearLayout inicio = findViewById(R.id.back_main);
        inicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryCourseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



        LinearLayout cuenta = findViewById(R.id.back_account);
        cuenta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("CategoryCourseActivity", "MI CUENTA button clicked");
                Intent intent = new Intent(CategoryCourseActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout favoritos = findViewById(R.id.cursos_favoritos);
        favoritos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("CategoryCourseActivity", "FAVORITOS button clicked");
                Intent intent = new Intent(CategoryCourseActivity.this, FavoriteCoursesActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getCoursesByCategory(int categoryId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Course>> call = apiService.getCoursesByCategory(categoryId);

        call.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Course> courses = response.body();

                    // Convert the response body to a JSON string
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(courses);

                    // Log the JSON response
                    Log.d(TAG, "JSON response: " + jsonResponse);

                    for (Course course : courses) {
                        Log.d(TAG, "Course: " + course.getName() + ", Description: " + course.getDescription());
                        // Verificar si el curso está marcado como favorito y establecer la bandera correspondiente
                        course.setFavorite(FavoriteCoursesManager.getInstance().isFavorite(course.getId()));
                    }

                    courseList.clear();
                    courseList.addAll(response.body());

                    // Log the size of the course list
                    Log.d(TAG, "Number of courses: " + courseList.size());

                    courseAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Adapter notified of data changes");

                } else {
                    Log.d(TAG, "Response unsuccessful or body is null. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(CategoryCourseActivity.this, "No se encontraron cursos en la categoría seleccionada.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Toast.makeText(CategoryCourseActivity.this, "Error al obtener los datos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al obtener los datos", t);
            }
        });
    }
}
