const express = require('express');
const { requireAuth } = require('../middleware/authMiddleware');
const exerciseController = require('../controllers/exerciseController');

const router = express.Router();

router.get('/', requireAuth, exerciseController.listExercises);

module.exports = router;
