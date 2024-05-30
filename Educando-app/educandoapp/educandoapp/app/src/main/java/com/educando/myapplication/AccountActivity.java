package com.educando.myapplication;

import static com.educando.myapplication.R.id.acc_go_main;
import static com.educando.myapplication.R.id.acc_go_cursos;
import static com.educando.myapplication.R.id.acc_go_recordatorios;


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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.UserDetailsResponse;
import com.educando.myapplication.api.UserSession;
import com.educando.myapplication.api.UsuarioCursosResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    TextView nombreTextView, apellidoTextView, emailTextView, cursosTextView, changePassButton;
    Button disconectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        nombreTextView = findViewById(R.id.acc_nombre_b);
        apellidoTextView = findViewById(R.id.acc_apellido_b);
        emailTextView = findViewById(R.id.acc_email_b);
        cursosTextView = findViewById(R.id.acc_curso_b);

        // Obtener el token de autenticación usando UserSession
        String authToken = UserSession.getInstance(this).getAuthToken();

        if (authToken != null) {
            // El token de autenticación está disponible, podemos usarlo para realizar solicitudes al servidor
            obtenerDetallesUsuario(authToken);
            // obtenerMisCursos(authToken); // Llamar al método para obtener los cursos del usuario
        } else {
            // El token de autenticación no está disponible, redirigir al usuario a iniciar sesión nuevamente
            Toast.makeText(AccountActivity.this, "Usuario no encontrado. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void obtenerDetallesUsuario(String authToken) {
        // Crear una instancia del servicio API utilizando Retrofit
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Crear un mapa para almacenar el token de autenticación
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", authToken);

        // Realizar la solicitud para obtener los detalles del usuario
        Call<UserDetailsResponse> callUsuario = apiService.obtenerUsuario(tokenMap);
        callUsuario.enqueue(new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                if (response.isSuccessful()) {
                    // Los detalles del usuario se obtuvieron con éxito
                    UserDetailsResponse userDetails = response.body();
                    if (userDetails != null) {
                        // Actualizar la interfaz de usuario con los detalles del usuario obtenidos
                        actualizarInterfazUsuario(userDetails);
                    }
                } else {
                    // Error al obtener los detalles del usuario
                    // Manejar el error según sea necesario
                    Toast.makeText(AccountActivity.this, "Error al obtener los detalles del usuario.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                // Error de red o de servidor al obtener los detalles del usuario
                // Manejar el error según sea necesario
                Toast.makeText(AccountActivity.this, "Error de red o de servidor al obtener los detalles del usuario.", Toast.LENGTH_SHORT).show();
                // Log.e("Error de red o de servidor", "Error al obtener los detalles del usuario: " + t.getMessage(), t);
            }
        });

        // Realizar la solicitud para obtener los cursos del usuario
        Call<List<UsuarioCursosResponse.CursoUsuario>> callCursos = apiService.obtenerUsuarioCursos(tokenMap);
        callCursos.enqueue(new Callback<List<UsuarioCursosResponse.CursoUsuario>>() {
            @Override
            public void onResponse(Call<List<UsuarioCursosResponse.CursoUsuario>> call, Response<List<UsuarioCursosResponse.CursoUsuario>> response) {
                if (response.isSuccessful()) {
                    // Los cursos del usuario se obtuvieron con éxito
                    List<UsuarioCursosResponse.CursoUsuario> cursos = response.body();
                    if (cursos != null) {

                        // Obtener el total de cursos
                        int totalCursos = cursos.size();

                        // Actualizar la interfaz de usuario con los cursos del usuario obtenidos
                        actualizarInterfazCursos(totalCursos);
                    }
                } else {
                    // Error al obtener los cursos del usuario
                    // Manejar el error según sea necesario
                    Toast.makeText(AccountActivity.this, "Error al obtener los cursos del usuario.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioCursosResponse.CursoUsuario>> call, Throwable t) {
                // Error de red o de servidor al obtener los cursos del usuario
                // Manejar el error según sea necesario
                Toast.makeText(AccountActivity.this, "Error de red o de servidor al obtener los cursos del usuario.", Toast.LENGTH_SHORT).show();
                Log.e("Error de red o de servidor", "Error al obtener los cursos del usuario: " + t.getMessage(), t);
            }
        });
    }

    private void actualizarInterfazUsuario(UserDetailsResponse userDetails) {
        // Actualizar la interfaz de usuario con los detalles del usuario obtenidos
        nombreTextView.setText(userDetails.getNombre());
        apellidoTextView.setText(userDetails.getApellido());
        emailTextView.setText(userDetails.getEmail());

        // Agregar un log para verificar la URL de la imagen
        Log.d("URL_IMAGEN", "URL de la imagen: " + userDetails.getUrlImagen());

        // Cargar la imagen de perfil utilizando Glide
        ImageView profileImageView = findViewById(R.id.imageView); // Cambia el ID según corresponda
        String urlImagen = userDetails.getUrlImagen();
        Glide.with(this)
                .load(urlImagen)
                .placeholder(R.drawable.name) // Imagen de placeholder mientras se carga la imagen
                .error(R.drawable.name) // Imagen de fallback en caso de error al cargar la imagen
                .into(profileImageView);

        // Configura el OnClickListener para el botón de cambiar contraseña
        changePassButton = findViewById(R.id.change_pass);
        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige al usuario a la actividad ChangePassActivity
                Intent intent = new Intent(AccountActivity.this, ChangePassActivity.class);
                startActivity(intent);
            }
        });

        // Configura el OnClickListener para el botón "Desconectar"
        disconectButton = findViewById(R.id.disconect);
        disconectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra la sesión y redirige al usuario a la pantalla de inicio de sesión
                UserSession.getInstance(AccountActivity.this).clearUserSession();
                Intent intent = new Intent(AccountActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Configura el OnClickListener para el botón "Contacto"
        LinearLayout contactButton = findViewById(R.id.contact);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige al usuario a la actividad ContactActivity
                Intent intent = new Intent(AccountActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout inicio = findViewById(acc_go_main);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí escribirás el código para iniciar la Main Activity
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout Recordatorios = findViewById(acc_go_recordatorios);
        Recordatorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, RecordatorioActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout cursos = findViewById(acc_go_cursos);
        cursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí escribirás el código para iniciar la Main Activity
                Intent intent = new Intent(AccountActivity.this, MyCourseActivity.class);
                startActivity(intent);
            }
        });

        // Obtén una referencia al botón "Abrir URL"
        LinearLayout openUrlButton = findViewById(R.id.buy_course);

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

    private void actualizarInterfazCursos(int totalCursos) {
        // Actualiza la interfaz con el total de cursos obtenidos
        String cursosText = "Total de cursos: " + totalCursos;
        cursosTextView.setText(cursosText);
    }

}