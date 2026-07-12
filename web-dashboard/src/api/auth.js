import { apiRequest } from './client';

export function createOrUpdateSession({ displayName, email }) {
  return apiRequest('/auth/session', { method: 'POST', body: { displayName, email } });
}
