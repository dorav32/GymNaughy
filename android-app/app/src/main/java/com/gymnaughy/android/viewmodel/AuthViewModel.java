package com.gymnaughy.android.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gymnaughy.android.model.User;
import com.gymnaughy.android.repository.AuthRepository;
import com.gymnaughy.android.repository.RepositoryCallback;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository = new AuthRepository();

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public boolean isSignedIn() {
        return authRepository.isSignedIn();
    }

    public void signIn(String email, String password) {
        loading.setValue(true);
        authRepository.signInWithEmail(email, password, callback());
    }

    public void register(String email, String password) {
        loading.setValue(true);
        authRepository.registerWithEmail(email, password, callback());
    }

    public void signInWithGoogle(String googleIdToken) {
        loading.setValue(true);
        authRepository.signInWithGoogle(googleIdToken, callback());
    }

    public void signOut() {
        authRepository.signOut();
        user.setValue(null);
    }

    @NonNull
    private RepositoryCallback<User> callback() {
        return new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User data) {
                loading.postValue(false);
                user.postValue(data);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        };
    }
}
