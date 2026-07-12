const express = require('express');
const { requireAuth } = require('../middleware/authMiddleware');
const authController = require('../controllers/authController');

const router = express.Router();

router.post('/session', requireAuth, authController.createOrUpdateSession);

module.exports = router;
