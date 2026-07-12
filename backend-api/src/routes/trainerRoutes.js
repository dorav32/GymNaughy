const express = require('express');
const { requireAuth, requireRole } = require('../middleware/authMiddleware');
const trainerController = require('../controllers/trainerController');

const router = express.Router();

router.use(requireAuth, requireRole('trainer'));

router.get('/roster', trainerController.getRoster);
router.get('/trainees/:uid', trainerController.getTraineeDetail);
router.put('/trainees/:uid/plan', trainerController.overrideTraineePlan);

module.exports = router;
