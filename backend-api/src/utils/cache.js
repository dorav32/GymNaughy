/**
 * Tiny in-memory TTL cache. Sufficient for a single-process API caching the
 * (rarely-changing) wger exercise catalog; would need to move to Redis if this
 * API ever ran as more than one instance.
 */
class TtlCache {
  constructor(ttlMs) {
    this.ttlMs = ttlMs;
    this.store = new Map();
  }

  get(key) {
    const entry = this.store.get(key);
    if (!entry) {
      return undefined;
    }
    if (Date.now() > entry.expiresAt) {
      this.store.delete(key);
      return undefined;
    }
    return entry.value;
  }

  set(key, value) {
    this.store.set(key, { value, expiresAt: Date.now() + this.ttlMs });
  }
}

module.exports = TtlCache;
