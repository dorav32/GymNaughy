import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import StatTile from '../components/StatTile';
import AdherenceChart from '../components/AdherenceChart';
import { getTraineeDetail } from '../api/trainees';

export default function TraineeDetailPage() {
  const { uid } = useParams();
  const [detail, setDetail] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getTraineeDetail(uid)
      .then(setDetail)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [uid]);

  return (
    <div className="layout">
      <Sidebar />
      <main className="main-content">
        <Link to="/" className="back-link">← Back to roster</Link>

        {loading && <p>Loading trainee…</p>}
        {error && <p className="error-text">{error}</p>}

        {detail && (
          <>
            <h1>{detail.displayName}</h1>
            <p className="trainee-detail__email">{detail.email}</p>

            <div className="stat-row">
              <StatTile label="Streak" value={`${detail.streak} days`} accent="#FF6F00" />
              <StatTile label="Total workouts" value={detail.totalWorkouts} />
              <StatTile label="Fitness level" value={detail.fitnessLevel || '—'} />
            </div>

            <section className="section">
              <div className="section__header">
                <h2>Training volume</h2>
              </div>
              <AdherenceChart history={detail.history || []} />
            </section>

            <section className="section">
              <div className="section__header">
                <h2>Current plan</h2>
                <Link to={`/trainees/${uid}/plan`} className="button-link">Edit plan</Link>
              </div>
              {detail.plan ? (
                <ul className="plan-day-list">
                  {detail.plan.days.map((day) => (
                    <li key={day.id}>
                      <strong>{day.label}</strong> — {day.exercises.length} exercises
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="empty-state">This trainee hasn't generated a plan yet.</p>
              )}
            </section>
          </>
        )}
      </main>
    </div>
  );
}
