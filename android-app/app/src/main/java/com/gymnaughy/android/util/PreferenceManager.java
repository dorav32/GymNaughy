package com.gymnaughy.android.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Local-only UI state that has no business living in Firestore (e.g. "has this device
 * already seen the onboarding flow"). Never used to cache server data — Firestore's own
 * offline cache handles that.
 */
public class PreferenceManager {

    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean hasCompletedOnboarding() {
        return prefs.getBoolean(Constants.PREF_KEY_ONBOARDING_DONE, false);
    }

    public void setOnboardingCompleted(boolean completed) {
        prefs.edit().putBoolean(Constants.PREF_KEY_ONBOARDING_DONE, completed).apply();
    }
}
