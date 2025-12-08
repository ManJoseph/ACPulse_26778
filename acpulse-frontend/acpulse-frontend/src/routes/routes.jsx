import { useRoutes, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import PublicRoute from './PublicRoute';

// Layouts
import MainLayout from '../components/layout/MainLayout';

// Pages
import Login from '../pages/Login';
import Signup from '../pages/Signup';
import ForgotPassword from '../pages/ForgotPassword';
import ResetPassword from '../pages/ResetPassword';
import Dashboard from '../pages/Dashboard';
import Rooms from '../pages/Rooms';
import Lecturers from '../pages/Lecturers';
import Profile from '../pages/Profile';
import Notifications from '../pages/Notifications';
import NotFound from '../pages/NotFound';
import ComponentTest from '../pages/ComponentTest'; // For testing

// Role-specific pages
import VerificationRequests from '../components/admin/VerificationRequests';
import UserManagement from '../components/admin/UserManagement';

export const AppRouter = () => {
  const routes = useRoutes([
    // --- Public Routes ---
    // Unauthenticated users are directed here.
    {
      element: <PublicRoute />,
      children: [
        { path: '/login', element: <Login /> },
        { path: '/signup', element: <Signup /> },
        { path: '/forgot-password', element: <ForgotPassword /> },
        { path: '/reset-password', element: <ResetPassword /> },
        // Redirect root to login for unauthenticated users
        { path: '/', element: <Navigate to="/login" replace /> },
      ],
    },

    // --- Protected Routes ---
    // Authenticated users are directed here, rendered within the main layout.
    {
      element: (
        <MainLayout>
          <ProtectedRoute />
        </MainLayout>
      ),
      children: [
        { path: '/dashboard', element: <Dashboard /> },
        { path: '/rooms', element: <Rooms /> },
        { path: '/lecturers', element: <Lecturers /> },
        { path: '/profile', element: <Profile /> },
        { path: '/notifications', element: <Notifications /> },
        {
          path: 'admin',
          children: [
            { path: 'verifications', element: <VerificationRequests /> },
            { path: 'users', element: <UserManagement /> },
          ],
        },
      ],
    },

    // --- Other Routes ---
    { path: '/test', element: <ComponentTest /> }, // Component showcase page
    { path: '*', element: <NotFound /> }, // 404 Not Found page
  ]);

  return routes;
};
