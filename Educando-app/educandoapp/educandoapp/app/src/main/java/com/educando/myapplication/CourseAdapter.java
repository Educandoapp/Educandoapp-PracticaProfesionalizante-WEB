package com.educando.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.educando.myapplication.R.id.pic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private List<Course> courses;
    DecimalFormat formatter;
    Context context;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
        formatter = new DecimalFormat("###,###,###,###.##");
    }

    @NonNull
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_course, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
        Course course = courses.get(position);

        Log.d(TAG, "Binding course at position " + position + ": " + course.getName());

        holder.titleTxt.setText(course.getName());
        // Limita la descripción a las primeras cinco palabras y agrega puntos suspensivos si es necesario
        holder.descriptionTxt.setText(limitDescription(course.getDescription()));
        holder.priceTxt.setText("$" + formatter.format(course.getPrice()));
        holder.durationTxt.setText(String.valueOf(course.getDuration()) + " horas");
        // holder.ratingTxt.setText(String.valueOf(course.getRating()));

        Glide.with(holder.itemView.getContext())
                .load(course.getImageUrl())
                .into(holder.pic);

        Log.d(TAG, "Course bound: " + course.getName());
    }

    // Método para limitar la descripción a las primeras cinco palabras y agregar puntos suspensivos si es necesario
    private String limitDescription(String description) {
        String[] words = description.split("\\s+");
        if (words.length > 5) {
            StringBuilder limitedDescription = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                limitedDescription.append(words[i]).append(" ");
            }
            return limitedDescription.toString().trim() + "...";
        } else {
            return description;
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, descriptionTxt, priceTxt, durationTxt, ratingTxt;
        ImageView pic;
        ImageButton favoriteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            descriptionTxt = itemView.findViewById(R.id.ownerTxt);
            priceTxt = itemView.findViewById(R.id.precioTxt);
            durationTxt = itemView.findViewById(R.id.timetxt);
            // ratingTxt = itemView.findViewById(R.id.ratingTxt);
            pic = itemView.findViewById(R.id.pic);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
}

