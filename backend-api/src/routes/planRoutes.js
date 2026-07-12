const express = require('express');
const { requireAuth } = require('../middleware/authMiddleware');
const planController = require('../controllers/planController');

const router = express.Router();

router.post('/generate', requireAuth, planController.generate);
router.get('/me', requireAuth, planController.getCurrent);

module.exports = router;
