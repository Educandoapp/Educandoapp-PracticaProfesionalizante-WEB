package com.educando.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.educando.myapplication.R.id.Cuenta;
import static com.educando.myapplication.R.id.main;


public class Recordatorio extends AppCompatActivity {

    private SharedPreferences preferencias;
    private List<String> listaTareas;
    private TareasAdapter adapter;
    private EditText editTextRecordatorio;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio);
        LinearLayout cuenta = findViewById(Cuenta);
        LinearLayout miscursos = findViewById(main);

        editTextRecordatorio = findViewById(R.id.editTextRecordatorio1);
        datePicker = findViewById(R.id.datePicker);

        preferencias = getSharedPreferences("MisRecordatorios", MODE_PRIVATE);

        // Mostrar la lista de tareas al iniciar la actividad
        mostrarListaTareas();

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
                Intent intent = new Intent(Recordatorio.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        miscursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recordatorio.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void guardarTarea(String tarea, String fechaSeleccionada) {
        SharedPreferences.Editor editor = preferencias.edit();
        int contadorTareas = preferencias.getAll().size();
        editor.putString("recordatorio_" + contadorTareas, tarea + "###" + fechaSeleccionada);
        editor.apply();
        mostrarListaTareas();
    }

    private String obtenerFechaActual() {
        // Implementación para obtener la fecha actual en el formato deseado
        // Puedes utilizar la clase java.text.SimpleDateFormat o java.time.LocalDate (API 26+)
        // Ejemplo con SimpleDateFormat:
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new java.util.Date());
    }

    private void mostrarListaTareas() {
        Map<String, ?> todasLasTareas = preferencias.getAll();
        listaTareas = new ArrayList<>();
        for (Map.Entry<String, ?> entrada : todasLasTareas.entrySet()) {
            if (entrada.getKey().startsWith("recordatorio_")) {
                listaTareas.add(entrada.getValue().toString());
            }
        }
        adapter = new TareasAdapter(listaTareas);
        RecyclerView recyclerViewTareas = findViewById(R.id.recyclerViewRecordatorios);
        if (recyclerViewTareas != null) {
            recyclerViewTareas.setAdapter(adapter);
            recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Toast.makeText(this, "Error al mostrar la lista de tareas", Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerFechaFormateada(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }
    public void guardarTareaOnClick(View view) {
        String tarea = editTextRecordatorio.getText().toString();
        if (!tarea.isEmpty()) {
            int dia = datePicker.getDayOfMonth();
            int mes = datePicker.getMonth();
            int anio = datePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(anio, mes, dia);
            String fechaSeleccionada = obtenerFechaFormateada(calendar.getTime());

            guardarTarea(tarea, fechaSeleccionada);
            Toast.makeText(this, "Tarea guardada correctamente", Toast.LENGTH_SHORT).show();
            editTextRecordatorio.setText("");
        } else {
            Toast.makeText(this, "Ingrese una tarea antes de guardar", Toast.LENGTH_SHORT).show();
        }
    }

    public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.TareasViewHolder> {
        private final List<String> listaTareas;

        public TareasAdapter(List<String> listaTareas) {
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
            TextView textView;
            TextView textViewTarea;
            TextView textViewFecha;

            ImageView imageViewDelete;

            TareasViewHolder(View itemView) {
                super(itemView);
                textViewTarea = itemView.findViewById(R.id.textViewTarea);
                textViewFecha = itemView.findViewById(R.id.textViewFecha); // Nuevo TextView para mostrar la fecha
                imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
                imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarTarea(getAdapterPosition());
                    }
                });
            }

            void bind(String tareaConFecha) {
                String[] partes = tareaConFecha.split("###");
                String tarea = partes[0];
                String fecha = partes[1];
                textViewTarea.setText(tarea);
                textViewFecha.setText(fecha); // Mostrar la fecha en el TextView correspondiente
            }
        }
    }

    private void eliminarTarea(int posicion) {
        String tareaAEliminar = listaTareas.get(posicion);
        SharedPreferences.Editor editor = preferencias.edit();
        Map<String, ?> todasLasTareas = preferencias.getAll();
        for (Map.Entry<String, ?> entrada : todasLasTareas.entrySet()) {
            if (entrada.getValue().toString().equals(tareaAEliminar)) {
                editor.remove(entrada.getKey());
                break;
            }
        }
        editor.apply();
        mostrarListaTareas();
    }
}
