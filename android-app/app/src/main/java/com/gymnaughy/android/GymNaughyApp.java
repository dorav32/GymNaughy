package com.gymnaughy.android;

import android.app.Application;

/**
 * Application entry point. Kept intentionally thin: Firebase initializes itself
 * from google-services.json, and every other singleton (Retrofit, repositories)
 * is lazily created on first access via {@link com.gymnaughy.android.network.ApiClient}.
 */
public class GymNaughyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
