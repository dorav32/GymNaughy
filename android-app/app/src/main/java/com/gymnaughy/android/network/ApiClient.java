package com.gymnaughy.android.network;

import com.gymnaughy.android.util.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Lazily-initialized singleton Retrofit client. There is exactly one instance for the
 * whole app so all repositories share the same connection pool and interceptors.
 */
public final class ApiClient {

    private static volatile ApiService instance;

    private ApiClient() {
    }

    public static ApiService getService() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = buildService();
                }
            }
        }
        return instance;
    }

    private static ApiService buildService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}
