package com.gymnaughy.android.firebase;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.tasks.Task;

/**
 * Thin wrapper around the Firebase Auth SDK. Kept separate from AuthRepository so the
 * raw SDK types (Task, AuthResult) never leak past the repository layer into ViewModels.
 */
public class FirebaseAuthManager {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public boolean isSignedIn() {
        return getCurrentUser() != null;
    }

    public Task<AuthResult> signInWithEmail(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> registerWithEmail(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInWithGoogle(String googleIdToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
        return firebaseAuth.signInWithCredential(credential);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}
