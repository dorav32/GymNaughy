const env = require('../config/env');
const logger = require('../utils/logger');
const TtlCache = require('../utils/cache');

const cache = new TtlCache(env.exerciseCacheTtlMs);

/**
 * Small, hand-picked fallback catalog. Used whenever the public wger.de API is
 * unreachable (rate limit, network hiccup, schema drift) so plan generation never
 * hard-fails just because a third party is down — the tradeoff documented in
 * docs/ARCHITECTURE.md.
 */
const FALLBACK_EXERCISES = [
  { id: 'ex_pushup', name: 'Push-up', muscleGroup: 'chest', equipment: ['bodyweight'], difficulty: 'beginner', instructions: 'Keep your core tight and lower your chest to the floor.', imageUrl: null },
  { id: 'ex_squat', name: 'Bodyweight Squat', muscleGroup: 'legs', equipment: ['bodyweight'], difficulty: 'beginner', instructions: 'Feet shoulder-width apart, sit back into your hips.', imageUrl: null },
  { id: 'ex_plank', name: 'Plank', muscleGroup: 'core', equipment: ['bodyweight'], difficulty: 'beginner', instructions: 'Hold a straight line from shoulders to ankles.', imageUrl: null },
  { id: 'ex_lunge', name: 'Walking Lunge', muscleGroup: 'legs', equipment: ['bodyweight'], difficulty: 'intermediate', instructions: 'Step forward and lower your back knee toward the floor.', imageUrl: null },
  { id: 'ex_db_bench_press', name: 'Dumbbell Bench Press', muscleGroup: 'chest', equipment: ['dumbbells'], difficulty: 'intermediate', instructions: 'Press dumbbells up from chest level, control the descent.', imageUrl: null },
  { id: 'ex_db_row', name: 'Dumbbell Row', muscleGroup: 'back', equipment: ['dumbbells'], difficulty: 'beginner', instructions: 'Hinge at the hips, pull the dumbbell to your ribcage.', imageUrl: null },
  { id: 'ex_db_shoulder_press', name: 'Dumbbell Shoulder Press', muscleGroup: 'shoulders', equipment: ['dumbbells'], difficulty: 'intermediate', instructions: 'Press dumbbells overhead without arching your lower back.', imageUrl: null },
  { id: 'ex_db_goblet_squat', name: 'Goblet Squat', muscleGroup: 'legs', equipment: ['dumbbells'], difficulty: 'beginner', instructions: 'Hold a dumbbell at chest height and squat between your knees.', imageUrl: null },
  { id: 'ex_band_row', name: 'Band Row', muscleGroup: 'back', equipment: ['resistance_bands'], difficulty: 'beginner', instructions: 'Anchor the band and pull toward your torso.', imageUrl: null },
  { id: 'ex_band_squat', name: 'Band Squat', muscleGroup: 'legs', equipment: ['resistance_bands'], difficulty: 'beginner', instructions: 'Step on the band and squat against its resistance.', imageUrl: null },
  { id: 'ex_barbell_squat', name: 'Barbell Back Squat', muscleGroup: 'legs', equipment: ['full_gym'], difficulty: 'advanced', instructions: 'Bar on your upper back, squat to depth, drive up through your heels.', imageUrl: null },
  { id: 'ex_barbell_deadlift', name: 'Barbell Deadlift', muscleGroup: 'back', equipment: ['full_gym'], difficulty: 'advanced', instructions: 'Hinge at the hips, keep the bar close, drive through the floor.', imageUrl: null },
  { id: 'ex_lat_pulldown', name: 'Lat Pulldown', muscleGroup: 'back', equipment: ['full_gym'], difficulty: 'beginner', instructions: 'Pull the bar to your upper chest, control the return.', imageUrl: null },
  { id: 'ex_leg_press', name: 'Leg Press', muscleGroup: 'legs', equipment: ['full_gym'], difficulty: 'beginner', instructions: 'Feet shoulder-width on the platform, press without locking your knees.', imageUrl: null },
];

/**
 * The four equipment buckets a trainee picks during onboarding are coarser than
 * wger's raw equipment names (e.g. "SZ-Bar", "Kettlebell", "Incline bench"), so we
 * map each raw name onto the closest bucket instead of requiring an exact string
 * match — otherwise almost nothing from the live API would ever match a user's
 * selection.
 */
function mapToEquipmentBuckets(rawNames) {
  const lower = (rawNames || []).map((n) => n.toLowerCase());
  const buckets = new Set();

  if (lower.length === 0 || lower.some((n) => n.includes('none') || n.includes('bodyweight'))) {
    buckets.add('bodyweight');
  }
  if (lower.some((n) => n.includes('dumbbell'))) {
    buckets.add('dumbbells');
  }
  if (lower.some((n) => n.includes('band'))) {
    buckets.add('resistance_bands');
  }
  if (lower.some((n) => /barbell|cable|machine|kettlebell|bench|bar\b|gym/.test(n))) {
    buckets.add('full_gym');
  }
  if (buckets.size === 0) {
    // Unrecognized equipment name: assume it needs full gym access rather than
    // silently excluding the exercise from every filter.
    buckets.add('full_gym');
  }
  return Array.from(buckets);
}

/**
 * The `/exerciseinfo/` endpoint nests category/equipment as objects (`{ name }`)
 * rather than the plain IDs the base `/exercise/` endpoint returns; this accepts
 * either shape so a wger API version change degrades gracefully instead of
 * silently mis-tagging every exercise as "general" / bodyweight-only.
 */
function extractName(value) {
  if (!value) return null;
  return typeof value === 'string' ? value : value.name;
}

function normalizeWgerExercise(raw) {
  const equipmentNames = (raw.equipment || []).map(extractName).filter(Boolean);
  return {
    id: `wger_${raw.id}`,
    name: raw.name,
    muscleGroup: (extractName(raw.category) || 'general').toLowerCase(),
    equipment: mapToEquipmentBuckets(equipmentNames),
    difficulty: 'intermediate',
    instructions: (raw.description || '').replace(/<[^>]+>/g, '').trim(),
    imageUrl: raw.image || null,
  };
}

/**
 * A trainee who has "full gym access" also has bodyweight, dumbbells, and bands
 * available — equipment access is additive, not a single exclusive category. This
 * expands a selected equipment list to everything it implies before filtering the
 * catalog, so "full_gym" surfaces the whole catalog instead of only the handful of
 * exercises explicitly tagged full_gym.
 */
const EQUIPMENT_HIERARCHY = {
  bodyweight: ['bodyweight'],
  resistance_bands: ['bodyweight', 'resistance_bands'],
  dumbbells: ['bodyweight', 'dumbbells'],
  full_gym: ['bodyweight', 'dumbbells', 'resistance_bands', 'full_gym'],
};

function expandEquipment(selected) {
  const expanded = new Set();
  (selected || []).forEach((item) => {
    (EQUIPMENT_HIERARCHY[item] || [item]).forEach((e) => expanded.add(e));
  });
  return Array.from(expanded);
}

async function fetchFromWger() {
  const url = `${env.wgerBaseUrl}/exerciseinfo/?language=2&limit=50`;
  const response = await fetch(url, { headers: { Accept: 'application/json' } });
  if (!response.ok) {
    throw new Error(`wger.de responded with ${response.status}`);
  }
  const body = await response.json();
  return (body.results || []).map(normalizeWgerExercise);
}

async function getCatalog() {
  const cached = cache.get('catalog');
  if (cached) {
    return cached;
  }

  try {
    const exercises = await fetchFromWger();
    const catalog = exercises.length > 0 ? exercises : FALLBACK_EXERCISES;
    cache.set('catalog', catalog);
    return catalog;
  } catch (err) {
    logger.warn('Falling back to local exercise catalog', { reason: err.message });
    cache.set('catalog', FALLBACK_EXERCISES);
    return FALLBACK_EXERCISES;
  }
}

async function getExercises({ equipment, muscleGroup } = {}) {
  const catalog = await getCatalog();
  const expandedEquipment = equipment && equipment.length > 0 ? expandEquipment(equipment) : undefined;

  return catalog.filter((exercise) => {
    const matchesEquipment = !expandedEquipment
      || exercise.equipment.some((e) => expandedEquipment.includes(e));
    const matchesMuscle = !muscleGroup || exercise.muscleGroup === muscleGroup.toLowerCase();
    return matchesEquipment && matchesMuscle;
  });
}

module.exports = { getExercises, getCatalog, FALLBACK_EXERCISES };
