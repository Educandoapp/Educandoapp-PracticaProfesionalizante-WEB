package com.educando.myapplication.api;

import retrofit2.Retrofit;

public class ApiManager {
    private static ApiService apiService;

    public static ApiService getService() {
        if (apiService == null) {
            Retrofit retrofit = ApiClient.getClient();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
