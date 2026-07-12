# API Specification — `backend-api`

Base URL (local): `http://localhost:4000/api`

All endpoints except `GET /health` require:

```
Authorization: Bearer <Firebase ID token>
```

The token is verified by `src/middleware/authMiddleware.js` using the Firebase Admin SDK. Endpoints marked **Trainer only** additionally require the custom claim `role: "trainer"` on the token.

---

## Auth

### `POST /api/auth/session`
Verifies the caller's token and creates/updates their Firestore profile on first login.

**Request body**
```json
{
  "displayName": "Dor Avraham",
  "email": "dor@example.com"
}
```

**Response `200`**
```json
{
  "uid": "f8c1...",
  "displayName": "Dor Avraham",
  "email": "dor@example.com",
  "role": "trainee",
  "fitnessLevel": null,
  "equipment": [],
  "createdAt": "2026-07-12T10:00:00.000Z"
}
```

---

## Exercises

### `GET /api/exercises?equipment=dumbbells,bodyweight&muscle=chest`
Returns exercises from the local cache, backed by the wger.de public exercise database.

**Response `200`**
```json
[
  {
    "id": "ex_bench_press",
    "name": "Dumbbell Bench Press",
    "muscleGroup": "chest",
    "equipment": ["dumbbells", "bench"],
    "difficulty": "intermediate",
    "instructions": "Lie on a flat bench, press dumbbells up from chest level...",
    "imageUrl": "https://wger.de/media/exercise-images/..."
  }
]
```

---

## Plans

### `POST /api/plans/generate`
Generates (and persists) a personalized `WorkoutPlan` for the caller.

**Request body**
```json
{
  "fitnessLevel": "beginner",
  "equipment": ["bodyweight", "dumbbells"],
  "goal": "hypertrophy",
  "daysPerWeek": 4
}
```

**Response `201`**
```json
{
  "id": "plan_20260712",
  "userId": "f8c1...",
  "fitnessLevel": "beginner",
  "goal": "hypertrophy",
  "daysPerWeek": 4,
  "days": [
    {
      "id": "day_1",
      "label": "Day 1 — Upper Body",
      "exercises": [
        { "exerciseId": "ex_pushup", "sets": 3, "reps": 12, "restSeconds": 60 },
        { "exerciseId": "ex_db_row", "sets": 3, "reps": 10, "restSeconds": 75 }
      ]
    }
  ],
  "createdAt": "2026-07-12T10:05:00.000Z"
}
```

### `GET /api/plans/me`
Returns the caller's current plan (`404` if none has been generated yet).

---

## Workouts

### `POST /api/workouts/log`
Logs a completed workout session and updates the caller's streak.

**Request body**
```json
{
  "planId": "plan_20260712",
  "dayId": "day_1",
  "completedSets": [
    { "exerciseId": "ex_pushup", "setsCompleted": 3, "repsCompleted": [12, 11, 10] }
  ],
  "durationSeconds": 1620
}
```

**Response `200`**
```json
{
  "logId": "log_9f3a",
  "streak": 5,
  "totalWorkouts": 23
}
```

### `GET /api/workouts/history?limit=30`
Returns the caller's most recent workout logs plus current streak.

---

## Trainer (Trainer only)

### `GET /api/trainer/roster`
```json
[
  {
    "uid": "f8c1...",
    "displayName": "Dor Avraham",
    "fitnessLevel": "beginner",
    "streak": 5,
    "adherencePct": 82,
    "lastWorkoutAt": "2026-07-11T18:22:00.000Z"
  }
]
```

### `GET /api/trainer/trainees/:uid`
Full detail for one trainee: profile, current plan, last 30 workout logs.

### `PUT /api/trainer/trainees/:uid/plan`
Trainer overrides a trainee's current plan. Body shape matches the `WorkoutPlan` object returned by `POST /api/plans/generate`.

---

## Errors

All errors follow the same shape (`src/middleware/errorHandler.js`):

```json
{
  "error": {
    "code": "PLAN_NOT_FOUND",
    "message": "No active workout plan for this user."
  }
}
```

| HTTP | code | Meaning |
|---|---|---|
| 401 | `UNAUTHENTICATED` | Missing/invalid Firebase ID token |
| 403 | `FORBIDDEN` | Valid token but missing required role |
| 404 | `PLAN_NOT_FOUND` / `TRAINEE_NOT_FOUND` | Resource does not exist |
| 422 | `VALIDATION_ERROR` | Request body failed schema validation |
| 500 | `INTERNAL_ERROR` | Unexpected server error |
