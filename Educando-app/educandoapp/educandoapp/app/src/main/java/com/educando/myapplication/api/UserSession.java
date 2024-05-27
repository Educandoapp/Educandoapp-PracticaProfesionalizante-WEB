package com.educando.myapplication.api;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static UserSession instance;

    private UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Método estático para obtener la instancia de UserSession
    public static UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context);
        }
        return instance;
    }

    // Método para guardar el token de autenticación en SharedPreferences
    public void setAuthToken(String authToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.apply();
    }

    // Método para obtener el token de autenticación desde SharedPreferences
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    // Método para borrar el token de autenticación al cerrar sesión
    public void clearAuthToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.apply();
    }
}
