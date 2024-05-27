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

import com.educando.myapplication.api.ApiManager;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.UserSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText txtemail, txtpassword;
    UserSession userSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar UserSession con el contexto de la actividad
        userSession = UserSession.getInstance(this);

        txtemail = findViewById(R.id.emailLogin);
        txtpassword = findViewById(R.id.passwordLogin);

        ImageView imageButton = findViewById(R.id.Login);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = txtemail.getText().toString();
                final String password = txtpassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Log.i("LoginActivity", "Ingresa email y contraseña");
                    mostrarMensajeError("Ingresa email y contraseña");
                    return;
                }

                // Crear una instancia del servicio API
                ApiService apiService = ApiManager.getService();

                // Crear un objeto Usuario con las credenciales ingresadas
                Usuario usuario = new Usuario(email, password);

                // Realizar la solicitud POST para iniciar sesión
                Call<Usuario> call = apiService.iniciarSesion(usuario);
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Usuario usuario = response.body();
                            Log.i("LoginActivity", "Inicio de sesión exitoso");

                            // Guardar token en la sesión del usuario
                            String token = usuario.getToken();
                            userSession.setAuthToken(token);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            mostrarMensajeError("Usuario o contraseña incorrectos.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        // Error en la solicitud HTTP, manejar el error
                    }
                });
            }
        });

        // Añadir el OnClickListener para el registro
        TextView registerTextView = findViewById(R.id.registerLogin1);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void mostrarMensajeError(String mensaje) {
        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}