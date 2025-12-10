import Button from '../common/Button';
import { useAuthStore } from '../../store/authStore';


const StudentDashboard = () => {
    const navigate = useNavigate();
    const { user } = useAuthStore(); // Moved inside the component function
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="space-y-8"
    >
      <div className="flex justify-between items-center">
        <div>
            <h1 className="text-3xl font-bold tracking-tight">Welcome, {user?.name}!</h1>
            <p className="text-gray-500 dark:text-gray-400 mt-1">
                Here's a quick overview of what's happening.
            </p>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="flex flex-col items-center justify-center p-6 text-center hover:shadow-lg transition-shadow">
            <DoorOpen className="w-12 h-12 text-primary-500 mb-4"/>
            <h3 className="text-lg font-semibold mb-2">Find a Room</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
                Check the status of any room on campus.
            </p>
            <Button onClick={() => navigate('/rooms')} className="w-full">
                <Search className="mr-2 h-4 w-4" /> Search Rooms
            </Button>
        </Card>
        <Card className="flex flex-col items-center justify-center p-6 text-center hover:shadow-lg transition-shadow">
            <Users className="w-12 h-12 text-secondary-500 mb-4"/>
            <h3 className="text-lg font-semibold mb-2">Find a Lecturer</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
                See if a lecturer is available in their office.
            </p>
            <Button onClick={() => navigate('/lecturers')} variant="secondary" className="w-full">
                <Search className="mr-2 h-4 w-4" /> Search Lecturers
            </Button>
        </Card>
         <Card className="flex flex-col items-center justify-center p-6 text-center bg-gradient-to-br from-primary-50 to-secondary-50 dark:from-dark-800 dark:to-dark-900">
            <h3 className="text-lg font-semibold mb-2">Need to get verified?</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
                If you are a lecturer or staff, you can request verification to access more features.
            </p>
            <Button onClick={() => navigate('/profile')} variant="outline" className="w-full">
                Go to Profile
            </Button>
        </Card>
      </div>
      
    </motion.div>
  );
};

export default StudentDashboard;
