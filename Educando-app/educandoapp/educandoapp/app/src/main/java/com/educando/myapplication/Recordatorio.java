package com.educando.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recordatorio extends AppCompatActivity {

    private SharedPreferences preferencias;
    private List<String> listaTareas;
    private TareasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio);

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
    }

    private void guardarTarea(String tarea) {
        SharedPreferences.Editor editor = preferencias.edit();
        int contadorTareas = preferencias.getAll().size();
        editor.putString("recordatorio_" + contadorTareas, tarea);
        editor.apply();
        mostrarListaTareas();
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

    public void guardarTareaOnClick(View view) {
        EditText editTextTarea = findViewById(R.id.editTextRecordatorio);
        String tarea = editTextTarea.getText().toString();
        if (!tarea.isEmpty()) {
            guardarTarea(tarea);
            Toast.makeText(this, "Tarea guardada correctamente", Toast.LENGTH_SHORT).show();
            editTextTarea.setText("");
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
            ImageView imageViewDelete;

            TareasViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textViewTarea);
                imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
                imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarTarea(getAdapterPosition());
                    }
                });
            }

            void bind(String tarea) {
                textView.setText(tarea);
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
