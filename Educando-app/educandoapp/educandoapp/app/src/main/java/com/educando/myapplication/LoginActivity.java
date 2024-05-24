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
import com.educando.myapplication.db.DbUsuarios;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText txtemail, txtpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

                            // Guarda el token y los datos del usuario (deberías encriptarlos antes de guardarlos)
                            String token = usuario.getToken();
                            // Guardar token y usuario en SharedPreferences o donde sea necesario

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
    }

    private void mostrarMensajeError(String mensaje) {
        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}