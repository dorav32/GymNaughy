import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Sidebar() {
  const { user, signOut } = useAuth();

  return (
    <aside className="sidebar">
      <div className="sidebar__brand">GymNaughy</div>
      <nav className="sidebar__nav">
        <NavLink to="/" end className={({ isActive }) => (isActive ? 'active' : '')}>
          Roster
        </NavLink>
      </nav>
      <div className="sidebar__footer">
        <div className="sidebar__user">{user?.email}</div>
        <button type="button" onClick={signOut}>Sign out</button>
      </div>
    </aside>
  );
}
