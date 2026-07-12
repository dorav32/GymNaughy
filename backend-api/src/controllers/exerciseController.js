const wgerService = require('../services/wgerService');

async function listExercises(req, res, next) {
  try {
    const equipment = req.query.equipment ? req.query.equipment.split(',') : undefined;
    const muscleGroup = req.query.muscle;
    const exercises = await wgerService.getExercises({ equipment, muscleGroup });
    res.status(200).json(exercises);
  } catch (err) {
    next(err);
  }
}

module.exports = { listExercises };
