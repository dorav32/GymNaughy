package com.gymnaughy.android.repository;

import com.google.firebase.firestore.ListenerRegistration;
import com.gymnaughy.android.firebase.FirebaseAuthManager;
import com.gymnaughy.android.firebase.FirestoreRepository;
import com.gymnaughy.android.firebase.OnDataChangeListener;
import com.gymnaughy.android.model.User;

/**
 * Exposes the signed-in user's profile as a real-time stream (direct Firestore read),
 * per the client-read exception documented in docs/ARCHITECTURE.md.
 */
public class UserRepository {

    private final FirebaseAuthManager authManager = new FirebaseAuthManager();
    private final FirestoreRepository firestoreRepository = new FirestoreRepository();

    public ListenerRegistration observeCurrentUser(OnDataChangeListener<User> listener) {
        String uid = authManager.getCurrentUser() != null ? authManager.getCurrentUser().getUid() : null;
        if (uid == null) {
            listener.onError(new IllegalStateException("No signed-in user"));
            return null;
        }
        return firestoreRepository.observeUser(uid, listener);
    }
}
