const test = require('node:test');
const assert = require('node:assert/strict');

const planGeneratorService = require('../src/services/planGeneratorService');

test('generates one day per requested daysPerWeek', async () => {
  const plan = await planGeneratorService.generatePlan({
    userId: 'test-user',
    fitnessLevel: 'beginner',
    equipment: ['bodyweight'],
    goal: 'hypertrophy',
    daysPerWeek: 3,
  });

  assert.equal(plan.days.length, 3);
  assert.equal(plan.userId, 'test-user');
});

test('every planned exercise has sets, reps, and rest defined', async () => {
  const plan = await planGeneratorService.generatePlan({
    userId: 'test-user',
    fitnessLevel: 'advanced',
    equipment: ['full_gym'],
    goal: 'strength',
    daysPerWeek: 4,
  });

  for (const day of plan.days) {
    assert.ok(day.exercises.length > 0, `day ${day.id} should have at least one exercise`);
    for (const exercise of day.exercises) {
      assert.ok(exercise.exerciseId);
      assert.ok(exercise.sets > 0);
      assert.ok(exercise.reps > 0);
      assert.ok(exercise.restSeconds > 0);
    }
  }
});
