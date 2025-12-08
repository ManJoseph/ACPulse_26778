import { Link } from 'react-router-dom';
import SignupForm from '../components/auth/SignupForm';
import Card from '../components/common/Card';

const Signup = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-dark-950 p-4">
      <div className="w-full max-w-2xl">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gradient-to-br from-primary-600 to-secondary-600 mb-4">
            <span className="text-white font-bold text-4xl">AC</span>
          </div>
          <h1 className="text-3xl font-bold gradient-text">Create Your ACPulse Account</h1>
          <p className="text-gray-500 dark:text-gray-400 mt-2">
            Join the AUCA Smart Campus community.
          </p>
        </div>

        <Card className="animate-slide-up">
          <Card.Body>
            <SignupForm />
          </Card.Body>
        </Card>

        <div className="mt-6 text-center text-sm">
          <p className="text-gray-600 dark:text-gray-400">
            Already have an account?{' '}
            <Link
              to="/login"
              className="font-medium text-primary-600 hover:text-primary-500"
            >
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Signup;
