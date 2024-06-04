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
import com.educando.myapplication.api.ApiClient;
import com.educando.myapplication.api.ApiService;
import com.educando.myapplication.api.CursoFavoritoRequest;
import com.educando.myapplication.api.UserSession;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private List<Course> courses;
    private boolean showFavoriteButton;
    DecimalFormat formatter;
    Context context;

    public CourseAdapter(List<Course> courses, boolean showFavoriteButton) {
        this.courses = courses;

        this.showFavoriteButton = showFavoriteButton;
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
        Log.d(TAG, "Detalles del curso:" +  course.getId());

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

        if (showFavoriteButton) {
            holder.favoriteButton.setVisibility(View.VISIBLE);
            actualizarBotonFavorito(holder.favoriteButton, course);

            holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean isFavorite = course.isFavorite(); // Declarar como final


                    if (isFavorite) {
                        // Si el curso está marcado como favorito, llama al método para eliminarlo
                        eliminarCursoFavorito(course, holder.favoriteButton);
                    } else {
                        // Si el curso no está marcado como favorito, llama al método para guardarlo como favorito
                        guardarCursoFavorito(course, holder.favoriteButton);
                    }
//                    // Actualizar el estado del curso como favorito o no favorito
//                    FavoriteCoursesManager.getInstance().setFavorite(course, !isFavorite);
//                    // Actualizar el icono del botón de favorito
//                    holder.favoriteButton.setImageResource(!isFavorite ? R.drawable.favorite : R.drawable.btn_2);
//                    // Llamar al método para guardar el estado de favorito en el backend
                }
            });
        } else {
            holder.favoriteButton.setVisibility(View.GONE);
        }

        Log.d(TAG, "Course bound: " + course.getName());
    }

    private void actualizarBotonFavorito(ImageButton favoriteButton, Course course) {
        if (FavoriteCoursesManager.getInstance().isFavorite(course.getId())) {
            favoriteButton.setImageResource(R.drawable.favorite);
        } else {
            favoriteButton.setImageResource(R.drawable.btn_2);
        }
    }

    private void guardarCursoFavorito(Course course, ImageButton favoriteButton) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Obtener el ID del curso
        int courseId = course.getId();

        // Obtener el ID del usuario logueado
        UserSession userSession = UserSession.getInstance(context);
        int userId = userSession.getUserId();

        // Crear una instancia de CursoFavoritoRequest con los IDs del curso y del usuario
        CursoFavoritoRequest request = new CursoFavoritoRequest(courseId, userId);

        // Llamar al método de ApiService para guardar el curso favorito utilizando el objeto request
        Call<Void> call = apiService.guardarCursoFavorito(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    course.setFavorite(true);
                    FavoriteCoursesManager.getInstance().setFavorite(course, true);
                    actualizarBotonFavorito(favoriteButton, course);
                    Log.d(TAG, "Curso guardado como favorito en el backend: " + course.getName());

                } else {
                    Log.d(TAG, "Error al guardar el curso como favorito en el backend. Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error al guardar el curso como favorito en el backend", t);
            }
        });
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

    public void eliminarCursoFavorito(Course course, ImageButton favoriteButton) {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int courseId = course.getId();
        Call<Void> call = apiService.eliminarCursoFavorito(courseId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    course.setFavorite(false);
                    FavoriteCoursesManager.getInstance().setFavorite(course, false);
                    actualizarBotonFavorito(favoriteButton, course);
                } else {
                    // Manejar el error si la eliminación falla
                    Log.d(TAG, "Error al eliminar el curso favorito. Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Manejar el error en caso de fallo de la llamada a la API
                Log.e(TAG, "Error al eliminar el curso favorito", t);
            }
        });
    }
}

