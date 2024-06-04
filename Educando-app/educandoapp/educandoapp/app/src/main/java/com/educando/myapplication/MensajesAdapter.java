package com.educando.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.educando.myapplication.api.ContactResponse;

import java.time.format.DateTimeParseException;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajeViewHolder> {
    private List<ContactResponse> mensajes;

    public MensajesAdapter(List<ContactResponse> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        ContactResponse mensaje = mensajes.get(position);

        Log.d("FechaMensaje", "Fecha del mensaje en adaptador: " + mensaje.getFecha());

        // Verifica si el título y mensaje son null y asigna un valor predeterminado en su lugar
        String titulo = mensaje.getTitulo() != null ? mensaje.getTitulo() : "Sin título";
        String mensajeTexto = mensaje.getMensaje() != null ? mensaje.getMensaje() : "Mensaje vacío";

        holder.tituloTextView.setText(titulo);
        holder.mensajeTextView.setText(mensajeTexto);

        // Parsear y mostrar la fecha usando DateTimeFormatter
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String fechaString = String.valueOf(mensaje.getFecha());
        if (fechaString != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(fechaString, inputFormatter);
                String formattedDate = dateTime.format(outputFormatter);
                holder.fechaTextView.setText(formattedDate);
                Log.d("FechaMensaje", "Fecha del mensaje después del parseo y formateo: " + formattedDate);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                Log.e("FechaMensaje", "Error al parsear la fecha: " + e.getMessage());
                holder.fechaTextView.setText("Fecha inválida");
            }
        } else {
            Log.e("FechaMensaje", "La fecha del mensaje es nula.");
            holder.fechaTextView.setText("Fecha inválida");
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    // Esta función te permitirá actualizar los mensajes en el adaptador
    public void setMensajes(List<ContactResponse> nuevosMensajes) {
        mensajes.clear();
        mensajes.addAll(nuevosMensajes);
        notifyDataSetChanged();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        public TextView tituloTextView;
        public TextView mensajeTextView;
        public TextView fechaTextView;

        public MensajeViewHolder(View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            mensajeTextView = itemView.findViewById(R.id.mensajeTextView);
            fechaTextView = itemView.findViewById(R.id.fechaTextView);
        }
    }
}

