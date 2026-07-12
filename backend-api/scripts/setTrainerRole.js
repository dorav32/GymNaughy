/**
 * One-off admin script: promotes a Firebase user to the 'trainer' custom claim and
 * mirrors the role onto their Firestore profile. There is no in-app "become a trainer"
 * flow by design (see docs/SETUP.md) — this is the only way to create a trainer.
 *
 * Usage: node scripts/setTrainerRole.js <firebase-uid>
 */
require('dotenv').config();
const { auth, db } = require('../src/config/firebase');

async function main() {
  const uid = process.argv[2];
  if (!uid) {
    console.error('Usage: node scripts/setTrainerRole.js <firebase-uid>');
    process.exit(1);
  }

  await auth.setCustomUserClaims(uid, { role: 'trainer' });
  await db.collection('users').doc(uid).set({ role: 'trainer' }, { merge: true });

  console.log(`User ${uid} is now a trainer. They must sign out and back in for the claim to take effect.`);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
