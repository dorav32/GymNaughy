import { apiRequest } from './client';

export function getRoster() {
  return apiRequest('/trainer/roster');
}

export function getTraineeDetail(uid) {
  return apiRequest(`/trainer/trainees/${uid}`);
}

export function overrideTraineePlan(uid, plan) {
  return apiRequest(`/trainer/trainees/${uid}/plan`, { method: 'PUT', body: plan });
}
