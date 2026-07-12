import { auth } from '../firebase';

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Thin fetch wrapper shared by every api/* module. Attaches the current Firebase
 * ID token the same way android-app's AuthInterceptor does, and normalizes
 * backend-api's { error: { code, message } } shape into a thrown Error.
 */
export async function apiRequest(path, { method = 'GET', body } = {}) {
  const currentUser = auth.currentUser;
  const headers = { 'Content-Type': 'application/json' };

  if (currentUser) {
    const idToken = await currentUser.getIdToken();
    headers.Authorization = `Bearer ${idToken}`;
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  const data = response.status === 204 ? null : await response.json();

  if (!response.ok) {
    const message = data && data.error ? data.error.message : `Request failed (${response.status})`;
    throw new Error(message);
  }

  return data;
}
