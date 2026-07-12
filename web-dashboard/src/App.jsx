import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RosterPage from './pages/RosterPage';
import TraineeDetailPage from './pages/TraineeDetailPage';
import PlanEditorPage from './pages/PlanEditorPage';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/"
            element={(
              <ProtectedRoute>
                <RosterPage />
              </ProtectedRoute>
            )}
          />
          <Route
            path="/trainees/:uid"
            element={(
              <ProtectedRoute>
                <TraineeDetailPage />
              </ProtectedRoute>
            )}
          />
          <Route
            path="/trainees/:uid/plan"
            element={(
              <ProtectedRoute>
                <PlanEditorPage />
              </ProtectedRoute>
            )}
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
