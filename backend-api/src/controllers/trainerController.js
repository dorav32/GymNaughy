const firestoreService = require('../services/firestoreService');

async function getRoster(req, res, next) {
  try {
    const roster = await firestoreService.getRoster(req.user.uid);
    res.status(200).json(roster);
  } catch (err) {
    next(err);
  }
}

async function getTraineeDetail(req, res, next) {
  try {
    const detail = await firestoreService.getTraineeDetail(req.params.uid, req.user.uid);
    res.status(200).json(detail);
  } catch (err) {
    next(err);
  }
}

async function overrideTraineePlan(req, res, next) {
  try {
    const plan = await firestoreService.overridePlan(req.params.uid, req.user.uid, req.body);
    res.status(200).json(plan);
  } catch (err) {
    next(err);
  }
}

module.exports = { getRoster, getTraineeDetail, overrideTraineePlan };
