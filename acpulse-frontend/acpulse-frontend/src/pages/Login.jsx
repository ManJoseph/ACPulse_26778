import { Link } from 'react-router-dom';
import LoginForm from '../components/auth/LoginForm';
import Card from '../components/common/Card';

const Login = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-dark-950 p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gradient-to-br from-primary-600 to-secondary-600 mb-4">
            <span className="text-white font-bold text-4xl">AC</span>
          </div>
          <h1 className="text-3xl font-bold gradient-text">Welcome to ACPulse</h1>
          <p className="text-gray-500 dark:text-gray-400 mt-2">
            Sign in to access the AUCA Smart Campus.
          </p>
        </div>

        <Card className="animate-slide-up">
          <Card.Body>
            <LoginForm />
          </Card.Body>
        </Card>

        <div className="mt-6 text-center text-sm">
          <p className="text-gray-600 dark:text-gray-400">
            Don't have an account?{' '}
            <Link
              to="/signup"
              className="font-medium text-primary-600 hover:text-primary-500"
            >
              Sign up
            </Link>
          </p>
          <p className="mt-2">
            <Link
              to="/forgot-password"
              className="font-medium text-sm text-primary-600 hover:text-primary-500"
            >
              Forgot your password?
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
