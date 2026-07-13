# Local setup guide

This walks through running all three subprojects against one Firebase project.

## 1. Create a Firebase project

1. Go to the [Firebase console](https://console.firebase.google.com) → **Add project** → name it `gymnaughy-dev`.
2. Enable **Authentication** → sign-in methods: **Email/Password** and **Google**.
3. Enable **Firestore** (production mode) in a nearby region.
4. Project settings → **Your apps** → add an **Android app** with package name `com.gymnaughy.android`, download `google-services.json`.
5. Project settings → **Service accounts** → **Generate new private key**, save as `backend-api/serviceAccountKey.json` (already git-ignored).
6. **Firestore Database → Rules tab** → paste the contents of [`firestore.rules`](../firestore.rules) → **Publish**. Firestore starts in deny-all mode, so without this step every direct client read from the Android app (dashboard, plan, progress) fails with a silent permission-denied error even though `backend-api` can still write fine through the Admin SDK.

## 2. `backend-api`

```bash
cd backend-api
npm install
cp .env.example .env
# edit .env: set GOOGLE_APPLICATION_CREDENTIALS=./serviceAccountKey.json
npm run dev
# API now listening on http://localhost:4000
```

Verify it's alive:

```bash
curl http://localhost:4000/api/health
```

### Assigning the trainer role

The first trainer account has to be promoted manually (there's no self-serve "become a trainer" flow, by design):

```bash
node scripts/setTrainerRole.js <trainer-firebase-uid>
```

## 3. `android-app`

1. Open `android-app/` in Android Studio (Giraffe or newer).
2. Drop the downloaded `google-services.json` into `android-app/app/`.
3. Edit `app/src/main/java/com/gymnaughy/android/util/Constants.java` and set `BASE_API_URL` to your backend (use `http://10.0.2.2:4000/api/` for the emulator talking to a backend on your host machine).
4. Run on an emulator or device (min SDK 24 / Android 7.0).

## 4. `web-dashboard`

```bash
cd web-dashboard
npm install
cp .env.example .env.local
# fill in the Firebase Web app config (Project settings → Your apps → Web app)
# and VITE_API_BASE_URL=http://localhost:4000/api
npm run dev
# Dashboard on http://localhost:5173
```

Log in with an account you promoted to `trainer` in step 2.

## 5. Running all three together

| Service | Command | Port |
|---|---|---|
| `backend-api` | `npm run dev` | 4000 |
| `web-dashboard` | `npm run dev` | 5173 |
| `android-app` | Run from Android Studio | — |

No Docker/compose setup is included on purpose — this is a portfolio-scale project meant to be read and run directly with each tool's native dev server.
