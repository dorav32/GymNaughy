const firestoreService = require('../services/firestoreService');
const ApiError = require('../utils/ApiError');

async function createOrUpdateSession(req, res, next) {
  try {
    const { displayName, email } = req.body;
    if (!email) {
      throw ApiError.validation('email is required.');
    }
    const user = await firestoreService.upsertUser(req.user.uid, { displayName, email });
    res.status(200).json(user);
  } catch (err) {
    next(err);
  }
}

module.exports = { createOrUpdateSession };
