export default function StatTile({ label, value, accent }) {
  return (
    <div className="stat-tile">
      <div className="stat-tile__value" style={accent ? { color: accent } : undefined}>
        {value}
      </div>
      <div className="stat-tile__label">{label}</div>
    </div>
  );
}
