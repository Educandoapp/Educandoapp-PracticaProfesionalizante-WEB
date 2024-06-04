package com.educando.myapplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FavoriteCoursesManager {
    private static FavoriteCoursesManager instance;
    private List<Course> favoriteCourses;

    private FavoriteCoursesManager() {
        favoriteCourses = new ArrayList<>();
    }

    public static synchronized FavoriteCoursesManager getInstance() {
        if (instance == null) {
            instance = new FavoriteCoursesManager();
        }
        return instance;
    }

    public void setFavoriteCourses(List<Course> favoriteCourses) {
        this.favoriteCourses.clear();
        this.favoriteCourses.addAll(favoriteCourses);
    }

    public boolean isFavorite(int courseId) {
        for (Course cursoFavorito : favoriteCourses) {
            if (cursoFavorito.getId() == courseId) {
                return true;
            }
        }
        return false;
    }

    public void setFavorite(Course cursoFavorito, boolean isFavorite) {
        if (isFavorite) {
            favoriteCourses.add(cursoFavorito);
        } else {
            favoriteCourses.remove(cursoFavorito);
        }
    }

    public List<Course> getFavoriteCourses() {
        return favoriteCourses;
    }

    public void removeFavoriteCourse(int courseId) {
        for (Iterator<Course> iterator = favoriteCourses.iterator(); iterator.hasNext();) {
            Course course = iterator.next();
            if (course.getId() == courseId) {
                iterator.remove();
                return; // Termina el bucle una vez que se elimina el curso
            }
        }
    }
}
