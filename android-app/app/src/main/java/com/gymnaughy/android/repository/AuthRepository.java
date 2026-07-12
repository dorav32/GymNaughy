package com.gymnaughy.android.repository;

import com.google.firebase.auth.FirebaseUser;
import com.gymnaughy.android.firebase.FirebaseAuthManager;
import com.gymnaughy.android.model.User;
import com.gymnaughy.android.network.ApiClient;
import com.gymnaughy.android.network.dto.SessionRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Single source of truth for "who is signed in". Every sign-in path (email, register,
 * Google) funnels through {@link #syncSession} so the Firestore user profile always
 * exists before the rest of the app (onboarding, dashboard) tries to read it.
 */
public class AuthRepository {

    private final FirebaseAuthManager authManager = new FirebaseAuthManager();

    public boolean isSignedIn() {
        return authManager.isSignedIn();
    }

    public void signInWithEmail(String email, String password, RepositoryCallback<User> callback) {
        authManager.signInWithEmail(email, password)
                .addOnSuccessListener(result -> syncSession(callback))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void registerWithEmail(String email, String password, RepositoryCallback<User> callback) {
        authManager.registerWithEmail(email, password)
                .addOnSuccessListener(result -> syncSession(callback))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void signInWithGoogle(String googleIdToken, RepositoryCallback<User> callback) {
        authManager.signInWithGoogle(googleIdToken)
                .addOnSuccessListener(result -> syncSession(callback))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void signOut() {
        authManager.signOut();
    }

    /**
     * Tells backend-api about the freshly-authenticated Firebase user so it can
     * create/update the Firestore profile document (writes to users/{uid} only ever
     * happen server-side — see docs/DATA_MODEL.md).
     */
    private void syncSession(RepositoryCallback<User> callback) {
        FirebaseUser firebaseUser = authManager.getCurrentUser();
        if (firebaseUser == null) {
            callback.onError("No signed-in user after successful auth call.");
            return;
        }

        SessionRequest request = new SessionRequest(firebaseUser.getDisplayName(), firebaseUser.getEmail());
        ApiClient.getService().createOrUpdateSession(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Session sync failed (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
