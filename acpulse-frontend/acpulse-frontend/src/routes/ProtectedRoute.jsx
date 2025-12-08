import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

/**
 * A route guard that checks for user authentication.
 * If the user is authenticated, it renders the child components.
 * Otherwise, it redirects the user to the login page.
 */
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuthStore();

  if (!isAuthenticated) {
    // If not authenticated, redirect to the login page
    return <Navigate to="/login" replace />;
  }

  // If authenticated, render the children (e.g., the MainLayout)
  return children;
};

export default ProtectedRoute;
