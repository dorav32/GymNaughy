const planGeneratorService = require('../services/planGeneratorService');
const firestoreService = require('../services/firestoreService');
const ApiError = require('../utils/ApiError');

const VALID_LEVELS = ['beginner', 'intermediate', 'advanced'];
const VALID_GOALS = ['strength', 'hypertrophy', 'endurance'];

async function generate(req, res, next) {
  try {
    const { fitnessLevel, equipment, goal, daysPerWeek } = req.body;

    if (!VALID_LEVELS.includes(fitnessLevel)) {
      throw ApiError.validation(`fitnessLevel must be one of ${VALID_LEVELS.join(', ')}.`);
    }
    if (!Array.isArray(equipment) || equipment.length === 0) {
      throw ApiError.validation('equipment must be a non-empty array.');
    }
    if (!VALID_GOALS.includes(goal)) {
      throw ApiError.validation(`goal must be one of ${VALID_GOALS.join(', ')}.`);
    }
    const days = parseInt(daysPerWeek, 10);
    if (!days || days < 1 || days > 6) {
      throw ApiError.validation('daysPerWeek must be between 1 and 6.');
    }

    const plan = await planGeneratorService.generatePlan({
      userId: req.user.uid,
      fitnessLevel,
      equipment,
      goal,
      daysPerWeek: days,
    });

    await firestoreService.savePlan(req.user.uid, plan);
    res.status(201).json(plan);
  } catch (err) {
    next(err);
  }
}

async function getCurrent(req, res, next) {
  try {
    const plan = await firestoreService.getCurrentPlan(req.user.uid);
    res.status(200).json(plan);
  } catch (err) {
    next(err);
  }
}

module.exports = { generate, getCurrent };
