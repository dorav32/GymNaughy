import { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import StatTile from '../components/StatTile';
import TraineeCard from '../components/TraineeCard';
import { getRoster } from '../api/trainees';

export default function RosterPage() {
  const [roster, setRoster] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getRoster()
      .then(setRoster)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const averageAdherence = roster.length === 0
    ? 0
    : Math.round(roster.reduce((sum, t) => sum + t.adherencePct, 0) / roster.length);

  return (
    <div className="layout">
      <Sidebar />
      <main className="main-content">
        <h1>Your roster</h1>

        {error && <p className="error-text">{error}</p>}
        {loading && <p>Loading roster…</p>}

        {!loading && !error && (
          <>
            <div className="stat-row">
              <StatTile label="Trainees" value={roster.length} />
              <StatTile label="Avg. adherence" value={`${averageAdherence}%`} accent="#2E7D32" />
            </div>

            {roster.length === 0 ? (
              <p className="empty-state">
                No trainees assigned yet — a trainee's <code>trainerId</code> field must point at
                your UID (see docs/DATA_MODEL.md).
              </p>
            ) : (
              <div className="trainee-grid">
                {roster.map((trainee) => (
                  <TraineeCard key={trainee.uid} trainee={trainee} />
                ))}
              </div>
            )}
          </>
        )}
      </main>
    </div>
  );
}
