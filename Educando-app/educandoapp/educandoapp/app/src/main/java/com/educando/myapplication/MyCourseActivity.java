package com.educando.myapplication;

import static com.educando.myapplication.R.id.acc_go_recordatorios;
import static com.educando.myapplication.R.id.back_main;
import static com.educando.myapplication.R.id.back_account;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
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

import com.bumptech.glide.Glide;
import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.UserSession;
import com.educando.myapplication.api.UsuarioCursosResponse;
import com.educando.myapplication.db.DbHelper;
import com.educando.myapplication.db.DbUsuarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.recyclerview.widget.DividerItemDecoration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCourseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Course> courseList;
    CourseAdapter courseAdapter;
    TypedArray courseImages;
    ApiService apiService;
    UserSession userSession;
    String authToken;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        Log.d("MyCourseActivity", "onCreate: Inicializando componentes de la interfaz de usuario");

        // Inicializa el RecyclerView
        try {
            recyclerView = findViewById(R.id.recycler_view1);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Agrega el sombreado inferior
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
            recyclerView.addItemDecoration(dividerItemDecoration);

            // Deshabilita clipToPadding para evitar el espaciado no deseado
            recyclerView.setClipToPadding(false);

            // Inicializa la lista de cursos
            courseList = new ArrayList<>();

            // Inicializa el adaptador
            courseAdapter = new CourseAdapter(courseList);

            // Configura el adaptador en el RecyclerView
            recyclerView.setAdapter(courseAdapter);
        } catch (Exception e) {
            Log.e("MyCourseActivity", "onCreate: Error al inicializar el RecyclerView", e);
        }

        // Inicializa UserSession
        userSession = UserSession.getInstance(this);

        // Obtiene el token de autenticación y el ID del usuario
        authToken = userSession.getAuthToken();
        userId = userSession.getUserId();

        Log.d("MyCourseActivity", "onCreate: authToken = " + authToken + ", userId = " + userId);

        if (authToken != null && userId != -1) {
            // Los datos del usuario están disponibles
            Log.d("MyCourseActivity", "onCreate: Datos del usuario encontrados, obteniendo cursos");
            obtenerCursosDelUsuario(authToken);
        } else {
            Log.d("MyCourseActivity", "onCreate: No se encontraron datos de usuario, mostrando lista vacía");
            // No se encontró un usuario logueado, redirige a la pantalla de inicio de sesión
            Toast.makeText(this, "Usuario no encontrado. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            // Intent intent = new Intent(MyCourseActivity.this, LoginActivity.class);
            // startActivity(intent);
            // finish();
        }

        LinearLayout inicio = findViewById(R.id.back_main);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCourseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout Recordatorios = findViewById(acc_go_recordatorios);
        Recordatorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCourseActivity.this, RecordatorioActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout cuenta = findViewById(R.id.back_account);
        cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCourseActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void obtenerCursosDelUsuario(String token) {
        Log.d("MyCourseActivity", "obtenerCursosDelUsuario: Iniciando llamada a la API");
        // Crear una instancia del servicio API utilizando Retrofit
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Crear un mapa para almacenar el token de autenticación
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", authToken);

        // Realiza la llamada al servicio API
        Call<List<UsuarioCursosResponse.CursoUsuario>> callCursos = apiService.obtenerUsuarioCursos(tokenMap);
        callCursos.enqueue(new Callback<List<UsuarioCursosResponse.CursoUsuario>>() {
            @Override
            public void onResponse(Call<List<UsuarioCursosResponse.CursoUsuario>> call, Response<List<UsuarioCursosResponse.CursoUsuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("MyCourseActivity", "onResponse: Llamada a la API exitosa, actualizando lista de cursos");
                    List<UsuarioCursosResponse.CursoUsuario> cursos = response.body();
                    for (UsuarioCursosResponse.CursoUsuario curso : cursos) {
                        Course course = new Course(curso.getNombreCurso(), curso.getDescripcionCurso(), 0, 0, 0.0f, curso.getImagenUrl());
                        courseList.add(course);
                    }
                    courseAdapter.notifyDataSetChanged();
                } else {
                    Log.d("MyCourseActivity", "onResponse: No se pudieron obtener los cursos, respuesta no exitosa");
                    Toast.makeText(MyCourseActivity.this, "No se pudieron obtener los cursos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioCursosResponse.CursoUsuario>> call, Throwable t) {
                Log.e("MyCourseActivity", "onFailure: Error al obtener los cursos", t);
                Toast.makeText(MyCourseActivity.this, "Error al obtener los cursos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            return new CourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
            Course course = courses.get(position);
            holder.bind(course, position);
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
            private Button buttonContinuar;

            public CourseViewHolder(View itemView) {
                super(itemView);
                courseNameTextView = itemView.findViewById(R.id.course_name);
                descriptionTextView = itemView.findViewById(R.id.course_description);
                courseImageView = itemView.findViewById(R.id.course_image);
                favoriteButton = itemView.findViewById(R.id.favoriteButton);
                favoriteButton.setImageResource(R.drawable.delete);
                buttonContinuar = itemView.findViewById(R.id.button_accion);
            }

            public void bind(Course course, int position) {
                courseNameTextView.setText(course.getName());
                descriptionTextView.setText(course.getDescription());
                buttonContinuar.setText("Continuar");
                Glide.with(itemView.getContext())
                        .load(course.getImageUrl())
                        .into(courseImageView);

                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // showConfirmationDialog(position);
                    }
                });

                buttonContinuar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://www.google.com";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        Log.d("MiApp", "Se hizo clic en el botón de compra");

                        if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                            v.getContext().startActivity(intent);
                        } else {
                            Toast.makeText(v.getContext(), "No se puede abrir la URL. Instala un navegador web.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

//        private void eliminarCursoDeBaseDeDatos(int position) {
//            if (position >= 0 && position < courseList.size()) {
//                Course course = courseList.get(position);
//                int courseId = obtenerIdCursoDesdeNombre(course.getName());
//
//                if (courseId != -1) {
//                    Usuario usuario = dbUsuarios.obtenerUsuarioLogueado();
//
//                    if (usuario != null) {
//                        SQLiteDatabase database = dbUsuarios.getWritableDatabase();
//
//                        if (database != null) {
//                            String whereClause = "id_curso = ? AND id_usuario = ?";
//                            String[] whereArgs = {String.valueOf(courseId), String.valueOf(usuario.getId_usuario())};
//
//                            int rowsDeleted = database.delete(DbHelper.TABLE_INTER_CUR_USER, whereClause, whereArgs);
//
//                            if (rowsDeleted > 0) {
//                                courseList.remove(position);
//                                courseAdapter.notifyItemRemoved(position);
//                            }
//                            database.close();
//                        }
//                    }
//                }
//            }
//        }

//        @SuppressLint("Range")
//        private int obtenerIdCursoDesdeNombre(String nombreCurso) {
//            int courseId = -1;
//            SQLiteDatabase database = dbUsuarios.getReadableDatabase();
//
//            if (database != null) {
//                String query = "SELECT id_curso FROM " + DbHelper.TABLE_CURSO + " WHERE nombre = ?";
//                Cursor cursor = database.rawQuery(query, new String[]{nombreCurso});
//
//                if (cursor != null) {
//                    if (cursor.moveToFirst()) {
//                        courseId = cursor.getInt(cursor.getColumnIndex("id_curso"));
//                    }
//                    cursor.close();
//                }
//                database.close();
//            }
//            return courseId;
//        }
//
//        private void showConfirmationDialog(final int position) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MyCourseActivity.this);
//            builder.setMessage("¿Estás seguro de que deseas eliminar este curso de tus favoritos?");
//            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    eliminarCursoDeBaseDeDatos(position);
//                }
//            });
//            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.show();
//        }
    }
}


//    // Adaptador para la lista de cursos
//    private class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
//
//        private List<Course> courses;
//
//        public CourseAdapter(List<Course> courses) {
//            this.courses = courses;
//        }
//
//        @NonNull
//        @Override
//        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
//            return new CourseViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
//            Course course = courses.get(position);
//            holder.bind(course, position);
//        }
//
//        @Override
//        public int getItemCount() {
//            return courses.size();
//        }
//
//        // ViewHolder para un curso
//        public class CourseViewHolder extends RecyclerView.ViewHolder {
//
//            private TextView courseNameTextView;
//            private TextView descriptionTextView;
//            private ImageView courseImageView;
//            private ImageButton favoriteButton;
//
//            private Button buttonCompra;
//
//            public CourseViewHolder(View itemView) {
//                super(itemView);
//                courseNameTextView = itemView.findViewById(R.id.course_name);
//                descriptionTextView = itemView.findViewById(R.id.course_description);
//                courseImageView = itemView.findViewById(R.id.course_image);
//                favoriteButton = itemView.findViewById(R.id.favoriteButton);
//                favoriteButton.setImageResource(R.drawable.delete);
//                buttonCompra = itemView.findViewById(R.id.button_compra);
//            }
//
//            public void bind(Course course, int position) {
//                courseNameTextView.setText(course.getName());
//                descriptionTextView.setText(course.getDescription());
//                // Obtener una imagen aleatoria para cada curso
//                int randomImageIndex = new Random().nextInt(courseImages.length());
//                int resourceId = courseImages.getResourceId(randomImageIndex, -1);
//
//                if (resourceId != -1) {
//                    courseImageView.setImageResource(resourceId);
//                }
//
//                favoriteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Mostrar un diálogo de confirmación antes de eliminar el curso
//                        showConfirmationDialog(position);
//                    }
//                });
//
//                // Configura la lógica del botón de compra aquí
//                buttonCompra.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Lógica de tu botón, por ejemplo, abrir una URL
//                        String url = "https://www.google.com";
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        Log.d("MiApp", "Se hizo clic en el botón de compra");
//
//                        if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
//                            v.getContext().startActivity(intent);
//                        } else {
//                            Toast.makeText(v.getContext(), "No se puede abrir la URL. Instala un navegador web.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }
//
//        // Método para eliminar cursos asignados al usuario de la base de datos
//        private void eliminarCursoDeBaseDeDatos(int position) {
//            if (position >= 0 && position < courseList.size()) {
//                Course course = courseList.get(position);
//                int courseId = obtenerIdCursoDesdeNombre(course.getName());
//
//                if (courseId != -1) {
//                    // Obtiene al usuario logueado
//                    Usuario usuario = dbUsuarios.obtenerUsuarioLogueado();
//
//                    if (usuario != null) {
//                        SQLiteDatabase database = dbUsuarios.getWritableDatabase();
//
//                        if (database != null) {
//                            // Define la cláusula WHERE para eliminar la asignación del curso con el ID correspondiente
//                            String whereClause = "id_curso = ? AND id_usuario = ?";
//                            String[] whereArgs = {String.valueOf(courseId), String.valueOf(usuario.getId_usuario())};
//
//                            // Ejecuta la sentencia SQL DELETE en la tabla de asignaciones
//                            int rowsDeleted = database.delete(DbHelper.TABLE_INTER_CUR_USER, whereClause, whereArgs);
//
//                            if (rowsDeleted > 0) {
//                                // Eliminación exitosa, ahora elimina el curso visualmente y de la lista
//                                courseList.remove(position);
//                                courseAdapter.notifyItemRemoved(position);
//                            }
//                            database.close();
//                        }
//                    }
//                }
//            }
//        }
//
//        @SuppressLint("Range")
//        private int obtenerIdCursoDesdeNombre(String nombreCurso) {
//            int courseId = -1;
//            SQLiteDatabase database = dbUsuarios.getReadableDatabase();
//
//            if (database != null) {
//                String query = "SELECT id_curso FROM " + DbHelper.TABLE_CURSO + " WHERE nombre = ?";
//                Cursor cursor = database.rawQuery(query, new String[]{nombreCurso});
//
//                if (cursor != null) {
//                    if (cursor.moveToFirst()) {
//                        courseId = cursor.getInt(cursor.getColumnIndex("id_curso"));
//                    }
//                    cursor.close();
//                }
//                database.close();
//            }
//            return courseId;
//        }
//
//        // Método para mostrar un diálogo de confirmación
//        private void showConfirmationDialog(final int position) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MyCourseActivity.this);
//            builder.setMessage("¿Estás seguro de que deseas eliminar este curso de tus favoritos?");
//            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    eliminarCursoDeBaseDeDatos(position);
//                }
//            });
//            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.show();
//        }
//    }
//}