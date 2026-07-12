import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

// Same Firebase project as android-app, so a trainer's account and custom
// `role: "trainer"` claim (set via backend-api/scripts/setTrainerRole.js) work here too.
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
};

export const firebaseApp = initializeApp(firebaseConfig);
export const auth = getAuth(firebaseApp);
