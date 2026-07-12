package com.gymnaughy.android.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}
