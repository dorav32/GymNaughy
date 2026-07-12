import { createContext, useContext, useEffect, useState } from 'react';
import {
  onAuthStateChanged,
  signInWithEmailAndPassword,
  signOut as firebaseSignOut,
} from 'firebase/auth';
import { auth } from '../firebase';
import { createOrUpdateSession } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (firebaseUser) => {
      setUser(firebaseUser);
      setLoading(false);
    });
    return unsubscribe;
  }, []);

  async function signIn(email, password) {
    setError(null);
    try {
      const credential = await signInWithEmailAndPassword(auth, email, password);
      // Mirrors AuthRepository.syncSession on the Android side: make sure
      // backend-api has an up-to-date profile before we hit any /trainer/* route.
      await createOrUpdateSession({
        displayName: credential.user.displayName || credential.user.email,
        email: credential.user.email,
      });
    } catch (err) {
      setError(err.message);
      throw err;
    }
  }

  async function signOut() {
    await firebaseSignOut(auth);
  }

  const value = { user, loading, error, signIn, signOut };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
