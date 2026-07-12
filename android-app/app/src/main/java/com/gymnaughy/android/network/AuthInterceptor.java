package com.gymnaughy.android.network;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Attaches the current Firebase ID token to every outgoing request as a Bearer token.
 * Runs on OkHttp's dispatcher thread, so blocking on {@link Tasks#await} here is safe —
 * it must never be called from the main thread.
 */
public class AuthInterceptor implements Interceptor {

    private static final long TOKEN_FETCH_TIMEOUT_SECONDS = 10;

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return chain.proceed(original);
        }

        String idToken;
        try {
            GetTokenResult result = Tasks.await(user.getIdToken(false), TOKEN_FETCH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            idToken = result.getToken();
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            // Fall back to an unauthenticated request; the API will respond 401
            // and the ViewModel layer surfaces that as a "please sign in again" state.
            return chain.proceed(original);
        }

        Request authorized = original.newBuilder()
                .header("Authorization", "Bearer " + idToken)
                .build();
        return chain.proceed(authorized);
    }
}
