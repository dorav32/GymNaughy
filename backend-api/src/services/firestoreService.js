const { db, admin } = require('../config/firebase');
const ApiError = require('../utils/ApiError');

const USERS = 'users';
const PLANS = 'plans';
const WORKOUT_LOGS = 'workout_logs';
const CURRENT_PLAN_DOC = 'current';

function userRef(uid) {
  return db.collection(USERS).doc(uid);
}

/**
 * Creates the Firestore profile on first login, or returns the existing one
 * untouched — this is the only place users/{uid} is ever written from outside
 * a workout/plan flow (see docs/DATA_MODEL.md security rules summary).
 */
async function upsertUser(uid, { displayName, email }) {
  const ref = userRef(uid);
  const snapshot = await ref.get();

  if (snapshot.exists) {
    return { uid, ...snapshot.data() };
  }

  const createdAt = admin.firestore.Timestamp.now();
  const newUser = {
    displayName: displayName || 'GymNaughy user',
    email: email || null,
    role: 'trainee',
    fitnessLevel: null,
    equipment: [],
    trainerId: null,
    streak: 0,
    totalWorkouts: 0,
    createdAt,
  };
  await ref.set(newUser);
  // Return an ISO string rather than the Firestore Timestamp we just wrote, since
  // this object goes straight into the JSON response (see docs/API_SPEC.md).
  return { uid, ...newUser, createdAt: createdAt.toDate().toISOString() };
}

async function getUser(uid) {
  const snapshot = await userRef(uid).get();
  if (!snapshot.exists) {
    throw ApiError.notFound('TRAINEE_NOT_FOUND', `No user profile for ${uid}.`);
  }
  return { uid, ...snapshot.data() };
}

async function savePlan(uid, plan) {
  await userRef(uid).collection(PLANS).doc(CURRENT_PLAN_DOC).set(plan);
  await userRef(uid).update({ fitnessLevel: plan.fitnessLevel });
  return plan;
}

async function getCurrentPlan(uid) {
  const snapshot = await userRef(uid).collection(PLANS).doc(CURRENT_PLAN_DOC).get();
  if (!snapshot.exists) {
    throw ApiError.notFound('PLAN_NOT_FOUND', 'No active workout plan for this user.');
  }
  return snapshot.data();
}

function isYesterday(date, reference) {
  const oneDayMs = 24 * 60 * 60 * 1000;
  const diffDays = Math.floor((reference.setHours(0, 0, 0, 0) - date.setHours(0, 0, 0, 0)) / oneDayMs);
  return diffDays === 1;
}

function isSameDay(a, b) {
  return a.toDateString() === b.toDateString();
}

/**
 * Logs a completed session and recomputes the streak: same-day logs don't change it,
 * a log the day after the last one extends it, anything older resets it to 1.
 */
async function addWorkoutLog(uid, logPayload) {
  const user = await getUser(uid);
  const now = new Date();
  const lastWorkoutAt = user.lastWorkoutAt ? user.lastWorkoutAt.toDate() : null;

  let nextStreak = 1;
  if (lastWorkoutAt) {
    if (isSameDay(lastWorkoutAt, now)) {
      nextStreak = user.streak;
    } else if (isYesterday(new Date(lastWorkoutAt), new Date(now))) {
      nextStreak = (user.streak || 0) + 1;
    }
  }

  const logRef = userRef(uid).collection(WORKOUT_LOGS).doc();
  const log = {
    ...logPayload,
    userId: uid,
    completedAt: admin.firestore.FieldValue.serverTimestamp(),
  };

  const totalWorkouts = (user.totalWorkouts || 0) + 1;

  await Promise.all([
    logRef.set(log),
    userRef(uid).update({
      streak: nextStreak,
      totalWorkouts,
      lastWorkoutAt: admin.firestore.FieldValue.serverTimestamp(),
    }),
  ]);

  return { logId: logRef.id, streak: nextStreak, totalWorkouts };
}

async function getWorkoutHistory(uid, limit = 30) {
  const snapshot = await userRef(uid).collection(WORKOUT_LOGS)
    .orderBy('completedAt', 'desc')
    .limit(limit)
    .get();

  // completedAt is a Firestore Timestamp in the document; convert it to the ISO
  // string both clients expect (WorkoutLog.completedAt is typed as a string in
  // docs/DATA_MODEL.md) rather than leaking the raw {seconds, nanoseconds} shape.
  return snapshot.docs.map((doc) => {
    const data = doc.data();
    return {
      id: doc.id,
      ...data,
      completedAt: data.completedAt ? data.completedAt.toDate().toISOString() : null,
    };
  });
}

/**
 * Trainer-only aggregation: never exposed through Firestore Security Rules directly,
 * only through this service running with the Admin SDK behind the trainer-role check
 * in authMiddleware.requireRole('trainer').
 */
async function getRoster(trainerUid) {
  const snapshot = await db.collection(USERS).where('trainerId', '==', trainerUid).get();

  const roster = await Promise.all(snapshot.docs.map(async (doc) => {
    const trainee = { uid: doc.id, ...doc.data() };
    const recentLogs = await getWorkoutHistory(doc.id, 7);
    const plan = await getCurrentPlan(doc.id).catch(() => null);
    const expectedPerWeek = plan ? plan.daysPerWeek : 7;
    const adherencePct = Math.min(100, Math.round((recentLogs.length / expectedPerWeek) * 100));

    return {
      uid: trainee.uid,
      displayName: trainee.displayName,
      fitnessLevel: trainee.fitnessLevel,
      streak: trainee.streak || 0,
      adherencePct,
      lastWorkoutAt: trainee.lastWorkoutAt ? trainee.lastWorkoutAt.toDate().toISOString() : null,
    };
  }));

  return roster;
}

async function getTraineeDetail(traineeUid, trainerUid) {
  const trainee = await getUser(traineeUid);
  if (trainee.trainerId !== trainerUid) {
    throw ApiError.forbidden('This trainee is not assigned to you.');
  }
  const [plan, history] = await Promise.all([
    getCurrentPlan(traineeUid).catch(() => null),
    getWorkoutHistory(traineeUid, 30),
  ]);
  return { ...trainee, plan, history };
}

async function overridePlan(traineeUid, trainerUid, plan) {
  const trainee = await getUser(traineeUid);
  if (trainee.trainerId !== trainerUid) {
    throw ApiError.forbidden('This trainee is not assigned to you.');
  }
  return savePlan(traineeUid, { ...plan, userId: traineeUid, updatedBy: trainerUid });
}

module.exports = {
  upsertUser,
  getUser,
  savePlan,
  getCurrentPlan,
  addWorkoutLog,
  getWorkoutHistory,
  getRoster,
  getTraineeDetail,
  overridePlan,
};
