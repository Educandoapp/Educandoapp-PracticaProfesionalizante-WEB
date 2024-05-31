package com.educando.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.educando.myapplication.R.id.acc_go_recordatorios;
import static com.educando.myapplication.R.id.back_account;
import static com.educando.myapplication.R.id.back_main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.SimpleCourse;
import com.educando.myapplication.api.UserSession;
import com.educando.myapplication.db.DbHelper;
import com.educando.myapplication.db.DbUsuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteCoursesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Course> courseList;
    CourseAdapterAcc courseAdapter;
    TypedArray courseImages;
    UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_favorites);

        Log.d("FavoriteCoursesActivity", "onCreate: Activity started");

        // Inicializa el RecyclerView
        recyclerView = findViewById(R.id.recycler_view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("FavoriteCoursesActivity", "onCreate: RecyclerView initialized");

        // Agrega el sombreado inferior
        androidx.recyclerview.widget.DividerItemDecoration dividerItemDecoration = new androidx.recyclerview.widget.DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        Log.d("FavoriteCoursesActivity", "onCreate: DividerItemDecoration added");

        // Deshabilita clipToPadding para evitar el espaciado no deseado
        recyclerView.setClipToPadding(false);
        Log.d("FavoriteCoursesActivity", "onCreate: ClipToPadding set to false");

        // Inicializa la lista de cursos
        courseList = new ArrayList<>();
        Log.d("FavoriteCoursesActivity", "onCreate: courseList initialized");

        // Inicializa el adaptador
        courseAdapter = new CourseAdapterAcc(courseList);

        // Configura el adaptador en el RecyclerView
        recyclerView.setAdapter(courseAdapter);
        Log.d("FavoriteCoursesActivity", "onCreate: Adapter set on RecyclerView");

        LinearLayout inicio = findViewById(R.id.back_main);
        inicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteCoursesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout cuenta = findViewById(R.id.back_account);
        cuenta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("CategoryCourseActivity", "MI CUENTA button clicked");
                Intent intent = new Intent(FavoriteCoursesActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        // Inicializa UserSession
        userSession = UserSession.getInstance(this);
        Log.d("FavoriteCoursesActivity", "onCreate: UserSession initialized");

        // Verifica si hay un usuario logueado
        if (userSession.isLoggedIn(this)) {
            // Obtener los cursos favoritos del usuario
            List<Course> favoriteCourses = FavoriteCoursesManager.getInstance().getFavoriteCourses();
            Log.d("FavoriteCoursesActivity", "onCreate: Favorite courses retrieved from FavoriteCoursesManager");

            // Llama a la API para obtener todos los cursos
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<List<Course>> call = apiService.getCourses();

            call.enqueue(new Callback<List<Course>>() {
                @Override
                public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Course> allCourses = response.body();

                        // Obtener los detalles de los cursos favoritos
                        for (Course course : allCourses) {
                            for (Course favoriteCourse : favoriteCourses) {
                                if (course.getId() == favoriteCourse.getId()) {
                                    // Agregar curso favorito con detalles al RecyclerView
                                    courseList.add(course);
                                    courseAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "Error al obtener todos los cursos");
                    }
                }

                @Override
                public void onFailure(Call<List<Course>> call, Throwable t) {
                    Log.e(TAG, "Error al realizar la llamada para obtener todos los cursos", t);
                }
            });

        } else {
            Log.d("FavoriteCoursesActivity", "onCreate: No user logged in");

            // Muestra un mensaje o redirige a la pantalla de inicio de sesión si no hay un usuario logueado
            Toast.makeText(this, "Por favor, inicie sesión para ver sus cursos favoritos", Toast.LENGTH_SHORT).show();
            // Puedes redirigir a la pantalla de inicio de sesión aquí si lo deseas
        }
    }

    // Adaptador para la lista de cursos
    private class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

        private List<Course> courses;

        public CourseAdapter(List<Course> courses) {
            this.courses = courses;
        }

        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
            Log.d("FavoriteCoursesActivity", "CourseAdapter: onCreateViewHolder called");
            return new CourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
            Course course = courses.get(position);
            holder.bind(course);
            Log.d("FavoriteCoursesActivity", "CourseAdapter: onBindViewHolder called for position " + position);

        }

        @Override
        public int getItemCount() {
            return courses.size();
        }

        // ViewHolder para un curso
        public class CourseViewHolder extends RecyclerView.ViewHolder {

            private TextView courseNameTextView;
            private TextView descriptionTextView;
            private ImageView courseImageView;
            private ImageButton favoriteButton;

            public CourseViewHolder(View itemView) {
                super(itemView);
                courseNameTextView = itemView.findViewById(R.id.course_name);
                descriptionTextView = itemView.findViewById(R.id.course_description);
                courseImageView = itemView.findViewById(R.id.course_image);
                favoriteButton = itemView.findViewById(R.id.favoriteButton);

            }

            public void bind(Course course) {
                courseNameTextView.setText(course.getName());
                descriptionTextView.setText(course.getDescription());
                Log.d("FavoriteCoursesActivity", "CourseViewHolder: bind called for course " + course.getName());

                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Llama a la API para eliminar el curso favorito
                        int courseId = course.getId();
                        ApiService apiService = ApiClient.getClient().create(ApiService.class);
                        Call<Void> call = apiService.eliminarCursoFavorito(courseId);
                        Log.d("FavoriteCoursesActivity", "CourseViewHolder: Favorite button clicked for course ID " + courseId);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    // Elimina el curso de la lista y notifica al adaptador
                                    int position = getAdapterPosition();
                                    if (position != RecyclerView.NO_POSITION) {
                                        courses.remove(position);
                                        notifyItemRemoved(position);
                                        Log.d("FavoriteCoursesActivity", "CourseViewHolder: Course removed from favorites");
                                        // Log para verificar la lista de cursos después de la eliminación
                                        Log.d("FavoriteCoursesActivity", "Courses after removal: " + courses.toString());

                                        // Actualiza la lista de cursos favoritos en el Singleton
                                        int courseId = course.getId();
                                        FavoriteCoursesManager.getInstance().removeFavoriteCourse(courseId);
                                        Log.d("FavoriteCoursesActivity", "CourseViewHolder: Course removed from favorites. Course ID: " + courseId);
                                        // Log para verificar la lista de cursos favoritos en el Singleton después de la eliminación
                                        Log.d("FavoriteCoursesActivity", "Favorite courses after removal: " + FavoriteCoursesManager.getInstance().getFavoriteCourses().toString());
                                    }
                                } else {
                                    Log.d("FavoriteCoursesActivity", "CourseViewHolder: Error in response, code: " + response.code());

                                    Toast.makeText(itemView.getContext(), "Error al eliminar el curso favorito", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d("FavoriteCoursesActivity", "CourseViewHolder: API call failed, error: " + t.getMessage());

                                Toast.makeText(itemView.getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }
}
