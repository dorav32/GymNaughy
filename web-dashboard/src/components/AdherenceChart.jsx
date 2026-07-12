import { CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';

/**
 * Plots total completed sets per logged session (oldest → newest), mirroring the
 * volume chart on the Android app's Progress screen so a trainer sees the same
 * signal their trainee does.
 */
export default function AdherenceChart({ history }) {
  const data = [...history]
    .filter((log) => log.completedAt)
    .sort((a, b) => new Date(a.completedAt) - new Date(b.completedAt))
    .map((log) => ({
      date: new Date(log.completedAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' }),
      sets: (log.completedSets || []).reduce((sum, s) => sum + (s.setsCompleted || 0), 0),
    }));

  if (data.length === 0) {
    return <p className="empty-state">No logged workouts yet.</p>;
  }

  return (
    <ResponsiveContainer width="100%" height={260}>
      <LineChart data={data} margin={{ top: 8, right: 16, bottom: 0, left: 0 }}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="date" />
        <YAxis allowDecimals={false} />
        <Tooltip />
        <Line type="monotone" dataKey="sets" name="Sets completed" stroke="#2E7D32" strokeWidth={2} dot={false} />
      </LineChart>
    </ResponsiveContainer>
  );
}
