/**
 * Minimal structured console logger. Kept dependency-free (no winston/pino) since
 * this API's log volume doesn't justify the extra surface for a portfolio project.
 */
function log(level, message, meta) {
  const entry = { level, message, timestamp: new Date().toISOString(), ...meta };
  // eslint-disable-next-line no-console
  console[level === 'error' ? 'error' : 'log'](JSON.stringify(entry));
}

module.exports = {
  info: (message, meta) => log('info', message, meta),
  warn: (message, meta) => log('warn', message, meta),
  error: (message, meta) => log('error', message, meta),
};
