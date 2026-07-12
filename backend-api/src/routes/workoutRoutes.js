const express = require('express');
const { requireAuth } = require('../middleware/authMiddleware');
const workoutController = require('../controllers/workoutController');

const router = express.Router();

router.post('/log', requireAuth, workoutController.logWorkout);
router.get('/history', requireAuth, workoutController.getHistory);

module.exports = router;
