import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Users, CheckCircle, BarChart2, AlertTriangle, DoorOpen } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import Card from '../common/Card';
import Button from '../common/Button';
import LoadingSpinner from '../common/LoadingSpinner';
import { adminService } from '../../services';
import { toast } from 'react-hot-toast';


const StatCard = ({ icon, label, value, color, loading }) => (
    <Card>
      <div className="flex items-center p-4 rounded-lg">
        <div className={`p-3 rounded-full bg-${color}-100 dark:bg-${color}-900/50`}>
          {React.cloneElement(icon, { className: `w-6 h-6 text-${color}-600 dark:text-${color}-400` })}
        </div>
        <div className="ml-4">
          {loading ? (
            <div className="h-7 w-16 bg-gray-200 dark:bg-dark-700 rounded-md animate-pulse" />
          ) : (
            <p className="text-2xl font-bold">{value}</p>
          )}
          <p className="text-sm text-gray-500 dark:text-gray-400">{label}</p>
        </div>
      </div>
    </Card>
  );

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [stats, setStats] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                setIsLoading(true);
                const data = await adminService.getDashboardStats();
                setStats(data);
            } catch (error) {
                toast.error('Failed to load dashboard statistics.');
                console.error(error);
            } finally {
                setIsLoading(false);
            }
        };
        fetchStats();
    }, []);

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="space-y-8"
    >
        <div>
            <h1 className="text-3xl font-bold tracking-tight">Admin Dashboard</h1>
            <p className="text-gray-500 dark:text-gray-400 mt-1">
                System overview and management.
            </p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <StatCard icon={<Users />} label="Total Users" value={stats?.totalUsers ?? '...'} color="primary" loading={isLoading} />
            <StatCard icon={<CheckCircle />} label="Pending Verifications" value={stats?.pendingVerifications ?? '...'} color="yellow" loading={isLoading} />
            <StatCard icon={<DoorOpen />} label="Occupied Rooms" value={stats?.occupiedRooms ?? '...'} color="green" loading={isLoading} />
            <StatCard icon={<BarChart2 />} label="System Health" value={stats?.systemHealth ?? '...'} color="blue" loading={isLoading} />
        </div>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card className="p-6 hover:shadow-lg transition-shadow">
                <div className="flex items-center gap-4">
                    <div className="p-3 rounded-full bg-secondary-100 dark:bg-secondary-900/50">
                        <Users className="w-6 h-6 text-secondary-600 dark:text-secondary-400" />
                    </div>
                    <div>
                        <h3 className="text-lg font-semibold">User Management</h3>
                        <p className="text-sm text-gray-500 dark:text-gray-400">Manage all registered users.</p>
                    </div>
                    <Button onClick={() => navigate('/admin/users')} className="ml-auto">Manage</Button>
                </div>
            </Card>
            <Card className="p-6 hover:shadow-lg transition-shadow">
            <div className="flex items-center gap-4">
                    <div className="p-3 rounded-full bg-yellow-100 dark:bg-yellow-900/50">
                        <CheckCircle className="w-6 h-6 text-yellow-600 dark:text-yellow-400" />
                    </div>
                    <div>
                        <h3 className="text-lg font-semibold">Verification Requests</h3>
                        <p className="text-sm text-gray-500 dark:text-gray-400">Approve or reject new requests.</p>
                    </div>
                    <Button onClick={() => navigate('/admin/verifications')} className="ml-auto" variant="outline">Review</Button>
                </div>
            </Card>
        </div>

    </motion.div>
  );
};

export default AdminDashboard;
