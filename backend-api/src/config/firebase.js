const admin = require('firebase-admin');

// Relies on GOOGLE_APPLICATION_CREDENTIALS (set in .env, see docs/SETUP.md) pointing at
// the service account key downloaded from the Firebase console. applicationDefault()
// picks it up automatically without us reading/parsing the file ourselves.
if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.applicationDefault(),
  });
}

module.exports = {
  admin,
  auth: admin.auth(),
  db: admin.firestore(),
};
