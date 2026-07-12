# Architecture

## 1. System overview

GymNaughy is split into three deployable units that share one backend contract:

```mermaid
flowchart TB
    subgraph Client Layer
        AND[Android App\nJava / MVVM]
        WEB[Trainer Dashboard\nReact / Vite]
    end

    subgraph API Layer
        GATE[Express App]
        MW[Auth Middleware\nverifies Firebase ID token]
        PLAN[Plan Generator Service]
        EXC[Exercise Service\n+ in-memory cache]
        WLOG[Workout Log Service]
        TRN[Trainer Aggregation Service]
    end

    subgraph Data Layer
        AUTH[(Firebase Auth)]
        FS[(Cloud Firestore)]
        WGER[(wger.de Exercise API)]
    end

    AND -->|HTTPS| GATE
    WEB -->|HTTPS| GATE
    GATE --> MW --> AUTH
    GATE --> PLAN --> EXC --> WGER
    GATE --> WLOG --> FS
    GATE --> TRN --> FS
    AND -.direct SDK reads for real-time UI.-> FS
    AND --> AUTH
    WEB --> AUTH
```

### Why the client talks to Firestore *and* the API

- **Writes that must be validated or aggregated** (workout logs, plan generation, trainer roster reads) go through `backend-api`, so business rules live in one place instead of being duplicated in Java and JS.
- **Read-mostly, latency-sensitive UI state** (today's plan once generated, streak counter) is read directly from Firestore by the Android client via the Firebase SDK, so the dashboard/progress screens update in real time and work offline using Firestore's local cache.
- Firestore Security Rules (see `docs/DATA_MODEL.md`) restrict every direct client read to the signed-in user's own documents; anything cross-user (trainer roster) is only exposed through the API, which runs with the Firebase Admin SDK and enforces the trainer role server-side.

## 2. Key flows

### 2.1 Onboarding → personalized plan generation

```mermaid
sequenceDiagram
    participant U as User (Android)
    participant A as Android App
    participant API as backend-api
    participant W as wger.de
    participant FS as Firestore

    U->>A: Selects fitness level + equipment
    A->>API: POST /api/plans/generate {level, equipment, goal}
    API->>W: GET /exercise?equipment=... (cache miss)
    W-->>API: Exercise catalog
    API->>API: PlanGeneratorService builds WorkoutPlan
    API->>FS: Save plan under users/{uid}/plans/current
    API-->>A: 201 Created + WorkoutPlan JSON
    A->>FS: Subscribe to users/{uid}/plans/current (real-time)
```

### 2.2 Completing a workout

```mermaid
sequenceDiagram
    participant A as Android App
    participant API as backend-api
    participant FS as Firestore

    A->>API: POST /api/workouts/log {planId, dayId, completedSets[]}
    API->>FS: Write workout_logs/{uid}/{logId}
    API->>API: Recalculate streak (consecutive days with a log)
    API->>FS: Update users/{uid}.streak
    API-->>A: 200 OK { streak, totalWorkouts }
```

### 2.3 Trainer roster view

```mermaid
sequenceDiagram
    participant T as Trainer (React dashboard)
    participant API as backend-api
    participant FS as Firestore

    T->>API: GET /api/trainer/roster (Authorization: Bearer <idToken>)
    API->>API: Verify token + assert role == "trainer"
    API->>FS: Query users where trainerId == caller.uid
    FS-->>API: Trainee profiles
    API->>FS: For each trainee, read workout_logs summary
    API-->>T: [{ id, name, streak, adherence, lastWorkout }]
```

## 3. Android app — MVVM layering

```
UI (Activity/Fragment)  →  ViewModel (LiveData)  →  Repository  →  { Retrofit ApiService, Firestore }
```

- **ViewModels** never touch Retrofit or Firestore directly — they only see a `Repository` interface, so the data source can be swapped (e.g. for tests) without touching UI code.
- **Repositories** are the single source of truth per domain (`AuthRepository`, `WorkoutRepository`, `UserRepository`) and decide whether a read goes to the network, Firestore, or a local cache.
- **AuthInterceptor** (OkHttp) attaches the current Firebase ID token to every request to `backend-api`, so the API can verify identity without a custom session/cookie mechanism.

## 4. Backend design decisions

- **Express over a full framework (Nest/etc.)**: the API surface is small (8 endpoints); a minimal framework keeps the code readable for a portfolio reviewer.
- **In-memory TTL cache in front of wger.de**: the external API is public and rate-limited-by-courtesy; caching exercise catalogs by equipment filter for 6 hours avoids hammering a third party and keeps plan generation fast.
- **Plan generation is deterministic given (level, equipment, goal, seed)**: this makes it unit-testable without mocking randomness.
- **Firebase Admin SDK is the only thing with write access to cross-user data** (trainer aggregation); the Android/React clients never query across users directly, which is enforced twice — once by Firestore Security Rules, once by the API layer.

## 5. React dashboard design decisions

- **Same Firebase project as the Android app** — a trainer logs in with the same identity system, and their custom claim `role: "trainer"` is what the API checks before returning roster data.
- **Vite + plain fetch client** (`src/api/client.js`) instead of a heavier data-fetching library, to keep the dependency surface small for a portfolio piece while still demonstrating clean separation between API access and components.
- **Recharts** for the adherence/volume charts — small API surface, good enough for a dashboard of this scope.
