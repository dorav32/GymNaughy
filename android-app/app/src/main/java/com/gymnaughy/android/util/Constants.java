package com.gymnaughy.android.util;

import com.gymnaughy.android.BuildConfig;

public final class Constants {

    /** Overridable per build type in app/build.gradle; defaults to the emulator loopback to a local backend-api. */
    public static final String BASE_API_URL = BuildConfig.BASE_API_URL;

    public static final String PREFS_NAME = "gymnaughy_prefs";
    public static final String PREF_KEY_ONBOARDING_DONE = "onboarding_done";

    private Constants() {
    }
}
