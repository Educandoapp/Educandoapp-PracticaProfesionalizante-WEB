package com.educando.myapplication.api;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_AUTH_TOKEN = "authToken";

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_FIRST_NAME = "userFirstName";
    private static final String KEY_USER_LAST_NAME = "userLastName";
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

    // Método para guardar el correo electrónico del usuario en SharedPreferences
    public void setUserEmail(String userEmail) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.apply();
    }

    // Método para obtener el correo electrónico del usuario desde SharedPreferences
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    // Método para borrar el correo electrónico del usuario al cerrar sesión
    public void clearUserEmail() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_EMAIL);
        editor.apply();
    }

    // Método para guardar el nombre y apellido del usuario en SharedPreferences
    public void setUserDetails(String userEmail, String userFirstName, String userLastName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_FIRST_NAME, userFirstName);
        editor.putString(KEY_USER_LAST_NAME, userLastName);
        editor.apply();
    }

    // Método para obtener el nombre del usuario desde SharedPreferences
    public String getUserFirstName() {
        return sharedPreferences.getString(KEY_USER_FIRST_NAME, null);
    }

    // Método para obtener el apellido del usuario desde SharedPreferences
    public String getUserLastName() {
        return sharedPreferences.getString(KEY_USER_LAST_NAME, null);
    }

    // Método para borrar el correo electrónico del usuario al cerrar sesión y borrar el token de autenticación
    public void clearUserSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_FIRST_NAME);
        editor.remove(KEY_USER_LAST_NAME);
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.apply();
    }

    // Método para guardar el correo electrónico, nombre y apellido del usuario en SharedPreferences
    public void setUserSession(String userEmail, String userFirstName, String userLastName, String authToken, int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_FIRST_NAME, userFirstName);
        editor.putString(KEY_USER_LAST_NAME, userLastName);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Método para guardar el ID del usuario en SharedPreferences
    public void setUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Método para obtener el ID del usuario desde SharedPreferences
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }
}
