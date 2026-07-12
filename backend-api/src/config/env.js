require('dotenv').config();

module.exports = {
  port: parseInt(process.env.PORT || '4000', 10),
  nodeEnv: process.env.NODE_ENV || 'development',
  wgerBaseUrl: process.env.WGER_BASE_URL || 'https://wger.de/api/v2',
  exerciseCacheTtlMs: (parseInt(process.env.EXERCISE_CACHE_TTL_HOURS || '6', 10)) * 60 * 60 * 1000,
};
