package com.educando.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.educando.myapplication.R.id.Cuenta;
import static com.educando.myapplication.R.id.main;

import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiManager;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.UserSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecordatorioActivity extends AppCompatActivity {

    private List<Recordatorio> listaTareas;
    private TareasAdapter adapter;
    private EditText editTextRecordatorio;
    private DatePicker datePicker;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio);
        LinearLayout cuenta = findViewById(Cuenta);
        LinearLayout miscursos = findViewById(main);

        editTextRecordatorio = findViewById(R.id.editTextRecordatorio);
        datePicker = findViewById(R.id.datePicker);

        // Inicializar la sesión de usuario
        userSession = UserSession.getInstance(this);

        // Inicializar la lista de tareas
        listaTareas = new ArrayList<>();

        // Configurar el RecyclerView
        RecyclerView recyclerViewTareas = findViewById(R.id.recyclerViewRecordatorios);
        adapter = new TareasAdapter(listaTareas);
        recyclerViewTareas.setAdapter(adapter);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));

        // Obtener recordatorios del backend al iniciar la actividad
        obtenerRecordatorios();

        setupDatePicker();

        // Obtener referencia al botón de guardar
        findViewById(R.id.botonGuardar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTareaOnClick(v);
            }
        });

        cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordatorioActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        miscursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordatorioActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void guardarTarea(String tarea, String fechaSeleccionada) {
        int idUsuario = userSession.getUserId(); // Obtener el idUsuario desde UserSession

        Log.d("RecordatorioActivity", "ID de usuario: " + idUsuario);

        // Convertir fecha al formato YYYY-MM-DD
        String formattedDate = formatDate(fechaSeleccionada);
        Log.d("RecordatorioActivity", "Fecha formateada: " + formattedDate);

        if (idUsuario != -1) {
            Recordatorio nuevoRecordatorio = new Recordatorio();
            nuevoRecordatorio.setIdUsuario(idUsuario); // Agrega un método setIdUsuario en la clase Recordatorio si es necesario
            nuevoRecordatorio.setTarea(tarea);
            nuevoRecordatorio.setFecha(formattedDate);

            Log.d("RecordatorioActivity", "Datos del recordatorio: " + nuevoRecordatorio.toString());

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Recordatorio> call = apiService.guardarRecordatorio(nuevoRecordatorio);
            call.enqueue(new Callback<Recordatorio>() {
                @Override
                public void onResponse(Call<Recordatorio> call, Response<Recordatorio> response) {
                    if (response.isSuccessful()) {
                        listaTareas.add(response.body());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(RecordatorioActivity.this, "Tarea guardada correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RecordatorioActivity.this, "Error al guardar la tarea", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Recordatorio> call, Throwable t) {
                    Toast.makeText(RecordatorioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para formatear la fecha
    private String formatDate(String fecha) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = inputFormat.parse(fecha);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return fecha;  // Devuelve la fecha original si hay un error
        }
    }

    private void obtenerRecordatorios() {
        int idUsuario = userSession.getUserId();

        ApiService service = ApiManager.getService();
        Call<List<Recordatorio>> call = service.getRecordatoriosPorUsuario(idUsuario);
        call.enqueue(new Callback<List<Recordatorio>>() {
            @Override
            public void onResponse(Call<List<Recordatorio>> call, Response<List<Recordatorio>> response) {
                if (response.isSuccessful()) {
                    listaTareas.clear();
                    listaTareas.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RecordatorioActivity.this, "Error al obtener los recordatorios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recordatorio>> call, Throwable t) {
                Toast.makeText(RecordatorioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("RecordatorioActivity", "Error de conexión: " + t.getMessage());
            }
        });
    }

    private void guardarTareaOnClick(View view) {
        String tarea = editTextRecordatorio.getText().toString();
        if (!tarea.isEmpty()) {
            int dia = datePicker.getDayOfMonth();
            int mes = datePicker.getMonth();
            int anio = datePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(anio, mes, dia);
            String fechaSeleccionada = obtenerFechaFormateada(calendar.getTime());

            guardarTarea(tarea, fechaSeleccionada);
            editTextRecordatorio.setText("");
        } else {
            Toast.makeText(this, "Ingrese una tarea antes de guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerFechaFormateada(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }

    public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.TareasViewHolder> {
        private final List<Recordatorio> listaTareas;

        public TareasAdapter(List<Recordatorio> listaTareas) {
            this.listaTareas = listaTareas;
        }

        @Override
        public TareasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
            return new TareasViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TareasViewHolder holder, int position) {
            holder.bind(listaTareas.get(position));
        }

        @Override
        public int getItemCount() {
            return listaTareas.size();
        }

        class TareasViewHolder extends RecyclerView.ViewHolder {
            TextView textViewTarea;
            TextView textViewFecha;

            ImageView imageViewDelete;

            TareasViewHolder(View itemView) {
                super(itemView);
                textViewTarea = itemView.findViewById(R.id.textViewTarea);
                textViewFecha = itemView.findViewById(R.id.textViewFecha);
                imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
                imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarTarea(getAdapterPosition());
                    }
                });
            }

            void bind(Recordatorio recordatorio) {
                textViewTarea.setText(recordatorio.getTarea());
                textViewFecha.setText(recordatorio.getFecha());
            }
        }
    }

    private void eliminarTarea(int position) {
        Recordatorio recordatorio = listaTareas.get(position);
        listaTareas.remove(position);
        adapter.notifyItemRemoved(position);

        ApiService service = ApiManager.getService();
        Call<Void> call = service.eliminarRecordatorio(recordatorio.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RecordatorioActivity.this, "Recordatorio eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecordatorioActivity.this, "Error al eliminar el recordatorio", Toast.LENGTH_SHORT).show();
                    Log.e("RecordatorioActivity", "Error al eliminar el recordatorio. Código de estado: " + response.code());
                    try {
                        Log.e("RecordatorioActivity", "Error al eliminar el recordatorio. Mensaje de error: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RecordatorioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("RecordatorioActivity", "Error de conexión: " + t.getMessage());
            }
        });
    }

    private void setupDatePicker() {
        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);

                        Calendar currentDate = Calendar.getInstance();

                        // Comprueba si la fecha seleccionada es anterior a la fecha actual
                        if (selectedDate.before(currentDate)) {
                            // Establece la fecha actual como fecha seleccionada
                            datePicker.init(
                                    currentDate.get(Calendar.YEAR),
                                    currentDate.get(Calendar.MONTH),
                                    currentDate.get(Calendar.DAY_OF_MONTH),
                                    this
                            );
                        }
                    }
                }
        );
    }
}
