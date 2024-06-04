package com.educando.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.RegisterRequest;
import com.educando.myapplication.api.RegisterResponse;
import com.educando.myapplication.db.DbUsuarios;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    EditText txtnombre, txtapellido, txtemail, txtpassword;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicialización de Retrofit para hacer llamadas a la API
        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        // Inicialización de las vistas EditText
        txtnombre = findViewById(R.id.txtnombre);
        txtapellido = findViewById(R.id.txtapellido);
        txtemail = findViewById(R.id.txtemail);
        txtpassword = findViewById(R.id.txtpassword);

        // Configuración del OnClickListener para el botón de registro (ImageView)
        ImageView imageView = findViewById(R.id.Register);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Registro del usuario en el backend
                String nombre = txtnombre.getText().toString();
                String apellido = txtapellido.getText().toString();
                String email = txtemail.getText().toString();
                String password = txtpassword.getText().toString();

                // Expresiones regulares para validar nombre, apellido y email
                String nombreRegex = "^[A-Za-z]+$"; // Solo letras
                String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"; // Formato de email

                // Verifica si los campos están vacíos
                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                } else if (!nombre.matches(nombreRegex) || !apellido.matches(nombreRegex)) {
                    // Validación para nombre y apellido
                    Toast.makeText(RegisterActivity.this, "Nombre y Apellido deben contener solo letras.", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailRegex)) {
                    // Validación para el formato de email
                    Toast.makeText(RegisterActivity.this, "El correo electrónico no tiene un formato válido.", Toast.LENGTH_SHORT).show();
                } else {
                    // Registro del usuario en el backend
                    registerUser(nombre, apellido, email, password);
                }
            }
        });

        // Configuración del OnClickListener para el TextView de iniciar sesión
        TextView textView2 = findViewById(R.id.textView2);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de inicio de sesión
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Cierra esta actividad
            }
        });
    }

    // Método para registrar al usuario en el backend
    private void registerUser(String nombre, String apellido, String email, String password) {
        // Crear objeto RegisterRequest con los datos del usuario
        RegisterRequest registerRequest = new RegisterRequest(nombre, apellido, email, password);

        registerRequest.setIdRolId(3); // Establecer el id_rol_id como 3 por defecto

        // Realizar la llamada a la API para registrar al usuario
        Call<RegisterResponse> call = apiService.registerUser(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    // Registro exitoso en el backend
                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Cierra esta actividad si el registro es exitoso
                } else {
                    // Error en el registro en el backend
                    Toast.makeText(RegisterActivity.this, "Error en el registro: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Error de conexión
                Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}