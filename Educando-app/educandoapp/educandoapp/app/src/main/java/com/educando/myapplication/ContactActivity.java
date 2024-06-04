package com.educando.myapplication;

import static com.educando.myapplication.R.id.back_account;
import static com.educando.myapplication.R.id.go_historial;
import static com.educando.myapplication.R.id.go_inicio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.educando.myapplication.api.ApiManager;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.ContactRequest;
import com.educando.myapplication.api.ContactResponse;
import com.educando.myapplication.api.UserSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity {

    private EditText txttitle;
    private EditText txtBlock;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Obtén referencias a los campos
        txttitle = findViewById(R.id.txttitle);
        txtBlock = findViewById(R.id.txtBlock);

        userSession = UserSession.getInstance(this);

        // Obtén referencias a los botones
        Button enviarButton = findViewById(R.id.btnEnviar);


        // Configura el clic del botón "Enviar" para validar y enviar el mensaje
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Puedes agregar aquí la lógica de envío si es necesario
                validarYEnviarMensaje();
            }
        });

        LinearLayout inicio = findViewById(go_inicio);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí escribirás el código para iniciar la MainActivity
                Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout cuenta = findViewById(back_account);
        cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí escribirás el código para iniciar la Main Activity
                Intent intent = new Intent(ContactActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        // Configura el clic del botón "Volver" para volver a AccountActivity
        LinearLayout historial = findViewById(go_historial);
        historial.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Aquí escribirás el código para iniciar la Main Activity
                Intent intent = new Intent(ContactActivity.this, MyHistorialActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validarYEnviarMensaje() {
        String titulo = txttitle.getText().toString();
        String mensaje = txtBlock.getText().toString();

        if (titulo.isEmpty() || mensaje.isEmpty()) {
            // Mostrar una alerta si alguno de los campos está vacío
            Toast.makeText(ContactActivity.this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Realizar el envío del mensaje aquí, si es necesario
            // Luego, inserta el mensaje en la base de datos
            enviarMensajeAlBackend(titulo, mensaje);
        }
    }

    private void enviarMensajeAlBackend(String titulo, String mensaje) {
        String email = userSession.getUserEmail();
        String nombre = userSession.getUserFirstName();
        String apellido = userSession.getUserLastName();

        // Verificar si el email y el nombre del usuario no son nulos antes de enviar el mensaje al backend
        if (email != null && nombre != null && apellido != null) {
            // Crear una instancia de ContactRequest con el email, nombre y mensaje
            ContactRequest contactRequest = new ContactRequest(email, nombre + " " + apellido, titulo, mensaje);

            // Obtener una instancia de ApiService a través de ApiManager
            ApiService apiService = ApiManager.getService();

            // Realizar la llamada al endpoint para enviar el mensaje de contacto
            Call<ContactResponse> call = apiService.enviarContacto(userSession.getAuthToken(), contactRequest);
            call.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Mostrar un mensaje de éxito si el mensaje se envió correctamente
                        mostrarMensajeEnviado();
                    } else {
                        // Mostrar un mensaje de error si hubo algún problema al enviar el mensaje
                        Toast.makeText(ContactActivity.this, "Error al enviar el mensaje de contacto.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    // Mostrar un mensaje de error si ocurrió un fallo en la conexión
                    Toast.makeText(ContactActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Mostrar un mensaje de error si el email o el nombre del usuario son nulos
            Toast.makeText(ContactActivity.this, "Error: no se pudo obtener el email o el nombre del usuario.", Toast.LENGTH_SHORT).show();
        }
    }


    private void mostrarMensajeEnviado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mensaje Enviado")
                .setMessage("Tu mensaje se ha enviado con éxito. Nos pondremos en contacto contigo pronto.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Cierra el cuadro de diálogo solo cuando el usuario hace clic en OK
                        // Después de cerrar el cuadro de diálogo, puedes continuar con la siguiente pantalla
                        Intent intent = new Intent(ContactActivity.this, AccountActivity.class);
                        startActivity(intent);
                    }
                })
                .setCancelable(false)
                .show();
    }
}
