import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import { getTraineeDetail, overrideTraineePlan } from '../api/trainees';

/**
 * Lets a trainer tweak sets/reps/rest on an already-generated plan and push the
 * override back through PUT /trainer/trainees/:uid/plan (see docs/API_SPEC.md).
 * Adding/removing whole days is out of scope here — trainees regenerate a plan
 * from scratch on the app for that.
 */
export default function PlanEditorPage() {
  const { uid } = useParams();
  const navigate = useNavigate();
  const [plan, setPlan] = useState(null);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getTraineeDetail(uid)
      .then((detail) => setPlan(detail.plan))
      .catch((err) => setError(err.message));
  }, [uid]);

  function updateExercise(dayIndex, exerciseIndex, field, value) {
    setPlan((prev) => {
      const next = structuredClone(prev);
      next.days[dayIndex].exercises[exerciseIndex][field] = Number(value);
      return next;
    });
  }

  async function handleSave() {
    setSaving(true);
    setError(null);
    try {
      await overrideTraineePlan(uid, plan);
      navigate(`/trainees/${uid}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="layout">
      <Sidebar />
      <main className="main-content">
        <Link to={`/trainees/${uid}`} className="back-link">← Back to trainee</Link>
        <h1>Edit plan</h1>

        {error && <p className="error-text">{error}</p>}
        {!plan && !error && <p>Loading plan…</p>}

        {plan && (
          <>
            {plan.days.map((day, dayIndex) => (
              <section key={day.id} className="section">
                <h2>{day.label}</h2>
                <table className="plan-editor-table">
                  <thead>
                    <tr>
                      <th>Exercise</th>
                      <th>Sets</th>
                      <th>Reps</th>
                      <th>Rest (s)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {day.exercises.map((exercise, exerciseIndex) => (
                      <tr key={exercise.exerciseId}>
                        <td>{exercise.exerciseId}</td>
                        <td>
                          <input
                            type="number"
                            min="1"
                            value={exercise.sets}
                            onChange={(e) => updateExercise(dayIndex, exerciseIndex, 'sets', e.target.value)}
                          />
                        </td>
                        <td>
                          <input
                            type="number"
                            min="1"
                            value={exercise.reps}
                            onChange={(e) => updateExercise(dayIndex, exerciseIndex, 'reps', e.target.value)}
                          />
                        </td>
                        <td>
                          <input
                            type="number"
                            min="0"
                            value={exercise.restSeconds}
                            onChange={(e) => updateExercise(dayIndex, exerciseIndex, 'restSeconds', e.target.value)}
                          />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </section>
            ))}

            <button type="button" onClick={handleSave} disabled={saving}>
              {saving ? 'Saving…' : 'Save changes'}
            </button>
          </>
        )}
      </main>
    </div>
  );
}
