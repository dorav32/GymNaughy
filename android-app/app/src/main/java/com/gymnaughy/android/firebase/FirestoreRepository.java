package com.gymnaughy.android.firebase;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.gymnaughy.android.model.User;
import com.gymnaughy.android.model.WorkoutPlan;

/**
 * Real-time reads that the Android client is allowed to perform directly against Firestore
 * (see docs/ARCHITECTURE.md §1 for why these specific reads bypass backend-api). All writes
 * to these same documents go through backend-api instead — Firestore Security Rules deny
 * client writes on these paths outright.
 */
public class FirestoreRepository {

    private static final String USERS_COLLECTION = "users";
    private static final String PLANS_SUBCOLLECTION = "plans";
    private static final String CURRENT_PLAN_DOC = "current";

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public ListenerRegistration observeUser(String uid, OnDataChangeListener<User> listener) {
        return firestore.collection(USERS_COLLECTION)
                .document(uid)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        listener.onError(error);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        listener.onChanged(snapshot.toObject(User.class));
                    }
                });
    }

    public ListenerRegistration observeCurrentPlan(String uid, OnDataChangeListener<WorkoutPlan> listener) {
        return firestore.collection(USERS_COLLECTION)
                .document(uid)
                .collection(PLANS_SUBCOLLECTION)
                .document(CURRENT_PLAN_DOC)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        listener.onError(error);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        listener.onChanged(snapshot.toObject(WorkoutPlan.class));
                    }
                });
    }
}
