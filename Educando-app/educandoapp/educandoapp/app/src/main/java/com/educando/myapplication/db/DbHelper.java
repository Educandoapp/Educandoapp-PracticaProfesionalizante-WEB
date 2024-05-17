package com.educando.myapplication.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.res.TypedArray;
import android.util.Log;

import com.educando.myapplication.CategoryDomain;
import com.educando.myapplication.CourseDomain;
import com.educando.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Define las constantes relacionadas con la base de datos
    private static final String DATABASE_NAME = "educando";

    // Define las constantes de las tablas
    public static final String TABLE_USUARIOS = "Usuario";
    public static final String TABLE_CATEGORIA = "Categoria";
    public static final String TABLE_CURSO = "Curso";
    public static final String TABLE_INTER_CUR_USER = "Inter_cur_user";
    public static final String TABLE_CONTACTO = "Contacto";

    // Clase interna para definir las constantes de la base de datos
    private static class DbConstants {
        private static final String HOST = "localhost";
        private static final String USER = "root";
        private static final String PASSWORD = "123456";
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // No es necesario crear tablas aquí, ya que estamos conectándonos a una base de datos externa
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // No es necesario realizar actualizaciones aquí, ya que estamos conectándonos a una base de datos externa
    }

    public List<CourseDomain> getAllCourses(Context context) {
        List<CourseDomain> courseList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase(); // Obtenemos la instancia de la base de datos

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CURSO, null);

            if (cursor.moveToFirst()) {
                do {
                    // Resto del código sin cambios
                } while (cursor.moveToNext());
            }

            cursor.close();
            database.close();
        } else {
            Log.e("DbHelper", "Database is null");
        }

        return courseList;
    }

    public List<CategoryDomain> getAllCategories(Context context) {
        List<CategoryDomain> categoryList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase(); // Obtenemos la instancia de la base de datos

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CATEGORIA, null);

            if (cursor.moveToFirst()) {
                do {
                    // Resto del código sin cambios
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();
        }
        return categoryList;
    }
}
