package com.educando.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.educando.myapplication.api.ApiManager;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.ContactResponse;
import com.educando.myapplication.api.UserSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyHistorialActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MensajesAdapter mensajesAdapter;

    // Crea una lista vacía de mensajes al inicio de tu actividad
    private List<ContactResponse> mensajesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        recyclerView = findViewById(R.id.recycler_post);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa el adaptador pasando la lista de mensajes
        mensajesAdapter = new MensajesAdapter(mensajesList);
        recyclerView.setAdapter(mensajesAdapter);

        // Carga los mensajes en un hilo aparte
        loadMensajesEnviados();

        LinearLayout cuenta = findViewById(R.id.back_account);
        cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí escribirás el código para iniciar la Main Activity
                Intent intent = new Intent(MyHistorialActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout post = findViewById(R.id.back_post1);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí escribirás el código para iniciar la Main Activity
                Intent intent = new Intent(MyHistorialActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout inicio = findViewById(R.id.go_inicio);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí escribirás el código para iniciar la MainActivity
                Intent intent = new Intent(MyHistorialActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadMensajesEnviados() {
        ApiService apiService = ApiManager.getService();
        String authToken = UserSession.getInstance(this).getAuthToken();
        String userEmail = UserSession.getInstance(this).getUserEmail();

        Call<List<ContactResponse>> call = apiService.obtenerContactos(userEmail);
        call.enqueue(new Callback<List<ContactResponse>>() {
            @Override
            public void onResponse(Call<List<ContactResponse>> call, Response<List<ContactResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ContactResponse> mensajes = response.body();

                    Collections.sort(mensajes, new Comparator<ContactResponse>() {
                        @Override
                        public int compare(ContactResponse mensaje1, ContactResponse mensaje2) {
                            // Compara las fechas en orden descendente
                            // Aquí deberías usar la fecha del mensaje en lugar de comparar los mensajes directamente
                            // return mensaje2.getFecha().compareTo(mensaje1.getFecha());
                            return 0; // reemplaza esto con la comparación de fechas
                        }
                    });

                    mensajesList.clear();
                    mensajesList.addAll(mensajes);
                    mensajesAdapter.notifyDataSetChanged();
                } else {
                    // Manejar errores de respuesta
                    Toast.makeText(MyHistorialActivity.this, "Error al obtener los mensajes enviados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ContactResponse>> call, Throwable t) {
                // Manejar errores de conexión
                Toast.makeText(MyHistorialActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

