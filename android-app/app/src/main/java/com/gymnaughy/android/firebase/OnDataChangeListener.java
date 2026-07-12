package com.gymnaughy.android.firebase;

/**
 * Callback for a real-time Firestore listener. {@code onChanged} may be called multiple
 * times over the lifetime of a subscription; {@code onError} is terminal for that listener.
 */
public interface OnDataChangeListener<T> {
    void onChanged(T data);
    void onError(Exception e);
}
