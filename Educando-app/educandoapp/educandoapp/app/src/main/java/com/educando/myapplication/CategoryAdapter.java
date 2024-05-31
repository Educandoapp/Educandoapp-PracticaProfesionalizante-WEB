package com.educando.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    Context context;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categories.get(position);
        holder.title_categoryTxt.setText(category.getName());

        // Obtener la URL de la imagen de la categoría
        String imageUrl = category.getImageUrl();

        // Cargar la imagen desde la URL usando Glide
        Glide.with(context)
                .load(imageUrl)
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title_categoryTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title_categoryTxt = itemView.findViewById(R.id.title_categoryTxt);
            pic = itemView.findViewById(R.id.pic);

            // Configura un OnClickListener para el elemento en el constructor del ViewHolder
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtén la categoría seleccionada
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Category selectedCategory = categories.get(position);

                        // Inicia la nueva actividad y pasa la categoría seleccionada como parámetro
                        Intent intent = new Intent(context, CategoryCourseActivity.class);
                        intent.putExtra("id_categoria", selectedCategory.getId());
                        intent.putExtra("nombre", selectedCategory.getName());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}