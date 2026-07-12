const firestoreService = require('../services/firestoreService');
const ApiError = require('../utils/ApiError');

async function logWorkout(req, res, next) {
  try {
    const { planId, dayId, completedSets, durationSeconds } = req.body;
    if (!planId || !dayId || !Array.isArray(completedSets)) {
      throw ApiError.validation('planId, dayId, and completedSets[] are required.');
    }

    const result = await firestoreService.addWorkoutLog(req.user.uid, {
      planId,
      dayId,
      completedSets,
      durationSeconds: durationSeconds || 0,
    });

    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
}

async function getHistory(req, res, next) {
  try {
    const limit = parseInt(req.query.limit, 10) || 30;
    const history = await firestoreService.getWorkoutHistory(req.user.uid, limit);
    res.status(200).json(history);
  } catch (err) {
    next(err);
  }
}

module.exports = { logWorkout, getHistory };
