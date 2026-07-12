const { auth } = require('../config/firebase');
const ApiError = require('../utils/ApiError');

/**
 * Verifies the Firebase ID token sent as `Authorization: Bearer <token>` by both the
 * Android app and the React dashboard, and attaches { uid, role } to req.user.
 * This is the one place identity is established for every downstream route.
 */
async function requireAuth(req, res, next) {
  const header = req.headers.authorization || '';
  const [scheme, token] = header.split(' ');

  if (scheme !== 'Bearer' || !token) {
    return next(ApiError.unauthenticated());
  }

  try {
    const decoded = await auth.verifyIdToken(token);
    req.user = {
      uid: decoded.uid,
      role: decoded.role || 'trainee',
    };
    return next();
  } catch (err) {
    return next(ApiError.unauthenticated('Firebase ID token could not be verified.'));
  }
}

/**
 * Route guard for trainer-only endpoints (roster, trainee detail/override). The
 * `trainer` claim is set once via scripts/setTrainerRole.js — there is no self-serve
 * upgrade path by design (see docs/SETUP.md).
 */
function requireRole(role) {
  return (req, res, next) => {
    if (!req.user || req.user.role !== role) {
      return next(ApiError.forbidden(`This endpoint requires the '${role}' role.`));
    }
    return next();
  };
}

module.exports = { requireAuth, requireRole };
