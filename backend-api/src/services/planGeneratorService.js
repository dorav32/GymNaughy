const wgerService = require('./wgerService');

const DAY_SPLITS = {
  1: ['full_body'],
  2: ['full_body', 'full_body'],
  3: ['push', 'pull', 'legs'],
  4: ['upper', 'lower', 'upper', 'lower'],
  5: ['chest', 'back', 'legs', 'shoulders', 'core'],
  6: ['chest', 'back', 'legs', 'shoulders', 'core', 'full_body'],
};

const MUSCLE_GROUPS_BY_FOCUS = {
  full_body: ['chest', 'back', 'legs', 'core'],
  push: ['chest', 'shoulders'],
  pull: ['back'],
  legs: ['legs'],
  upper: ['chest', 'back', 'shoulders'],
  lower: ['legs', 'core'],
  chest: ['chest'],
  back: ['back'],
  shoulders: ['shoulders'],
  core: ['core'],
};

const GOAL_PRESETS = {
  strength: { sets: 5, reps: 5, restSeconds: 120 },
  hypertrophy: { sets: 4, reps: 10, restSeconds: 75 },
  endurance: { sets: 3, reps: 18, restSeconds: 45 },
};

const EXERCISES_PER_DAY_BY_LEVEL = {
  beginner: 3,
  intermediate: 4,
  advanced: 5,
};

/**
 * Deterministic given the same (fitnessLevel, equipment, goal, daysPerWeek) and
 * exercise catalog snapshot — no randomness — so it's straightforward to unit test
 * and so the same inputs always reproduce the same plan for support/debugging.
 */
async function generatePlan({ userId, fitnessLevel, equipment, goal, daysPerWeek }) {
  const exercises = await wgerService.getExercises({ equipment });
  const split = DAY_SPLITS[daysPerWeek] || DAY_SPLITS[4];
  const preset = GOAL_PRESETS[goal] || GOAL_PRESETS.hypertrophy;
  const exercisesPerDay = EXERCISES_PER_DAY_BY_LEVEL[fitnessLevel] || EXERCISES_PER_DAY_BY_LEVEL.beginner;

  const days = split.map((focus, index) => {
    const targetMuscles = MUSCLE_GROUPS_BY_FOCUS[focus] || [focus];
    const candidates = exercises.filter((ex) => targetMuscles.includes(ex.muscleGroup));
    const pool = candidates.length >= exercisesPerDay ? candidates : exercises;

    const selected = pool.slice(0, exercisesPerDay).map((exercise) => ({
      exerciseId: exercise.id,
      sets: preset.sets,
      reps: preset.reps,
      restSeconds: preset.restSeconds,
    }));

    return {
      id: `day_${index + 1}`,
      label: `Day ${index + 1} — ${focus.replace(/_/g, ' ')}`,
      exercises: selected,
    };
  });

  return {
    id: `plan_${Date.now()}`,
    userId,
    fitnessLevel,
    goal,
    daysPerWeek,
    days,
    createdAt: new Date().toISOString(),
  };
}

module.exports = { generatePlan };
