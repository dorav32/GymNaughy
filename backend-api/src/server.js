// Loaded before anything else so GOOGLE_APPLICATION_CREDENTIALS (read by
// config/firebase.js, several requires down the chain from ./app) is already
// in process.env by the time Firebase Admin resolves it.
require('dotenv').config();

const app = require('./app');
const env = require('./config/env');
const logger = require('./utils/logger');

app.listen(env.port, () => {
  logger.info(`GymNaughy backend-api listening on port ${env.port}`, { env: env.nodeEnv });
});
