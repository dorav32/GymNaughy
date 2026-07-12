import { Link } from 'react-router-dom';

export default function TraineeCard({ trainee }) {
  return (
    <Link to={`/trainees/${trainee.uid}`} className="trainee-card">
      <div className="trainee-card__header">
        <span className="trainee-card__name">{trainee.displayName}</span>
        <span className="trainee-card__level">{trainee.fitnessLevel || 'no level set'}</span>
      </div>
      <div className="trainee-card__stats">
        <span>🔥 {trainee.streak} day streak</span>
        <span>✅ {trainee.adherencePct}% adherence</span>
      </div>
      <div className="trainee-card__last-workout">
        Last workout: {trainee.lastWorkoutAt ? new Date(trainee.lastWorkoutAt).toLocaleDateString() : '—'}
      </div>
    </Link>
  );
}
