package com.gymnaughy.android.network;

/**
 * Minimal success/error wrapper returned by repositories to ViewModels, so a failed
 * network or Firestore call never has to be represented as a null value or an exception
 * that crosses the ViewModel boundary.
 *
 * @param <T> the payload type on success
 */
public final class ApiResult<T> {

    private final T data;
    private final String errorMessage;

    private ApiResult(T data, String errorMessage) {
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(null, message);
    }

    public boolean isSuccess() {
        return errorMessage == null;
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
