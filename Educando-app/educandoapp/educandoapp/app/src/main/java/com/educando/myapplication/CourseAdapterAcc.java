package com.educando.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiService;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseAdapterAcc extends RecyclerView.Adapter<CourseAdapterAcc.ViewHolder> {
    private List<Course> courses;
    Context context;

    public CourseAdapterAcc(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseAdapterAcc.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapterAcc.ViewHolder holder, int position) {
        Course course = courses.get(position);
        Log.d("FavoriteCoursesActivity", "onBindViewHolder: Binding course at position " + position + " with name " + course.getName());

        Log.d(TAG, "Detalles del curso:" + course.getId());

        Log.d(TAG, "Binding course at position " + position + ": " + course.getName());

        holder.titleTxt.setText(course.getName());
        holder.descriptionTxt.setText(limitDescription(course.getDescription()));

        Glide.with(holder.itemView.getContext())
                .load(course.getImageUrl())
                .into(holder.pic);

        holder.favoriteButton.setVisibility(View.VISIBLE);
        holder.favoriteButton.setImageResource(R.drawable.favorite);
        holder.continueButton.setText("Comprar");

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    eliminarCursoFavorito(course, holder.favoriteButton, adapterPosition);
                }
            }
        });

        holder.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define la URL que deseas abrir
                String url = "http://10.0.2.2:4200"; // Reemplaza con tu URL real

                // Crea un intent para abrir la URL en un navegador web
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // Obtén el contexto del botón a través de la vista (v)
                Context context = v.getContext();

                // Llama al método startActivity() usando el contexto obtenido
                context.startActivity(intent);
            }
        });

        Log.d(TAG, "Course bound: " + course.getName());
    }

//    private void actualizarBotonFavorito(ImageButton favoriteButton, Course course) {
//        if (FavoriteCoursesManager.getInstance(context).isFavorite(course.getId())) {
//            favoriteButton.setImageResource(R.drawable.favorite);
//        } else {
//            favoriteButton.setImageResource(R.drawable.btn_2);
//        }
//    }

    private void eliminarCursoFavorito(Course course, ImageButton favoriteButton, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int courseId = course.getId();
        Call<Void> call = apiService.eliminarCursoFavorito(courseId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    course.setFavorite(false);
                    // Actualiza la lista de cursos favoritos en el Singleton
                    int courseId = course.getId();
                    FavoriteCoursesManager.getInstance().removeFavoriteCourse(courseId);
                    Log.d("FavoriteCoursesActivity", "CourseViewHolder: Course removed from favorites. Course ID: " + courseId);
                    // Log para verificar la lista de cursos favoritos en el Singleton después de la eliminación
                    Log.d("FavoriteCoursesActivity", "Favorite courses after removal: " + FavoriteCoursesManager.getInstance().getFavoriteCourses().toString());
                    courses.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, courses.size());
                    Log.d(TAG, "Curso eliminado de favoritos: " + course.getName());
                } else {
                    Log.d(TAG, "Error al eliminar el curso favorito. Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error al eliminar el curso favorito", t);
            }
        });
    }

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
        TextView titleTxt, descriptionTxt;
        ImageView pic;
        ImageButton favoriteButton;
        AppCompatButton continueButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.course_name);
            descriptionTxt = itemView.findViewById(R.id.course_description);
            pic = itemView.findViewById(R.id.course_image);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            continueButton = itemView.findViewById(R.id.button_accion);
        }
    }
}
