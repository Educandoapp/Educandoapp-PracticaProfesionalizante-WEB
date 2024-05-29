package com.educando.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.UserDetailsResponse;
import com.educando.myapplication.api.UserSession;
import com.educando.myapplication.db.DbUsuarios;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassActivity extends AppCompatActivity {

    EditText oldPasswordEditText, newPassword1EditText, newPassword2EditText;
    Button changePassword, cancelButton;
    ApiService apiService;
    UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPasswordEditText = findViewById(R.id.oldpass);
        newPassword1EditText = findViewById(R.id.txtchangepass1);
        newPassword2EditText = findViewById(R.id.txtchangepass2);
        changePassword = findViewById(R.id.changePassword);
        cancelButton = findViewById(R.id.cancelButton);
        apiService = ApiClient.getClient().create(ApiService.class);
        userSession = UserSession.getInstance(this);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener contraseñas ingresadas por el usuario
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword1 = newPassword1EditText.getText().toString();
                String newPassword2 = newPassword2EditText.getText().toString();

                // Validar que las nuevas contraseñas coincidan
                if (!newPassword1.equals(newPassword2)) {
                    Toast.makeText(ChangePassActivity.this, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Obtener el id del usuario
                int userId = userSession.getUserId();
                if (userId == -1) {
                    Toast.makeText(ChangePassActivity.this, "Usuario no logueado", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear el payload para la solicitud
                JsonObject payload = new JsonObject();
                payload.addProperty("old_password", oldPassword);
                payload.addProperty("password", newPassword1);

                Call<UserDetailsResponse> call = apiService.updatePassword(userId, payload);
                call.enqueue(new Callback<UserDetailsResponse>() {
                    @Override
                    public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ChangePassActivity.this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangePassActivity.this, AccountActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ChangePassActivity.this, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                        Toast.makeText(ChangePassActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Terminar la actividad y volver a la anterior
                finish();
            }
        });
    }
}
