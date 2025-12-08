import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { DoorOpen, Calendar, Edit } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { toast } from 'react-hot-toast';

import Card from '../common/Card';
import Button from '../common/Button';
import Modal from '../common/Modal';
import Select from '../common/Select';
import Input from '../common/Input';
import { useAuthStore } from '../../store/authStore';
import { lecturerService } from '../../services';
import { LECTURER_STATUS } from '../../utils/constants';


const UpdateStatusModal = ({ isOpen, onClose, currentStatus }) => {
    const { register, handleSubmit, formState: { isSubmitting } } = useForm({
        defaultValues: {
            status: currentStatus?.status || LECTURER_STATUSES.AVAILABLE,
            message: currentStatus?.message || '',
        }
    });
    const { setUser, user } = useAuthStore();

    const onSubmit = async (data) => {
        try {
            const updatedStatus = await lecturerService.updateStatus(data);
            // Update user in store to reflect new status
            setUser({ ...user, status: updatedStatus });
            toast.success('Status updated successfully!');
            onClose();
        } catch (error) {
            toast.error(error.message || 'Failed to update status.');
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Update Your Status">
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <Select
                    label="Current Status"
                    id="status"
                    {...register('status')}
                >
                    {Object.values(LECTURER_STATUSES).map(status => (
                        <option key={status} value={status}>{status}</option>
                    ))}
                </Select>

                <Input
                    label="Custom Message (Optional)"
                    id="message"
                    placeholder="e.g., In a meeting until 3 PM"
                    {...register('message')}
                />

                <Modal.Footer
                    onCancel={onClose}
                    onConfirm={handleSubmit(onSubmit)}
                    confirmText="Update Status"
                    loading={isSubmitting}
                />
            </form>
        </Modal>
    );
};


const LecturerDashboard = () => {
    const navigate = useNavigate();
    const { user } = useAuthStore();
    const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <>
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
                Manage your status and schedule here.
            </p>
        </div>
        <Button onClick={() => setIsModalOpen(true)}>
            <Edit className="mr-2 h-4 w-4" /> Update Status
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card className="flex flex-col items-center justify-center p-6 text-center hover:shadow-lg transition-shadow">
            <Calendar className="w-12 h-12 text-primary-500 mb-4"/>
            <h3 className="text-lg font-semibold mb-2">My Schedule</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
                View your teaching schedule for the semester.
            </p>
            <Button onClick={() => navigate('/schedule')} className="w-full">
                View Schedule
            </Button>
        </Card>
        <Card className="flex flex-col items-center justify-center p-6 text-center hover:shadow-lg transition-shadow">
            <DoorOpen className="w-12 h-12 text-secondary-500 mb-4"/>
            <h3 className="text-lg font-semibold mb-2">Room Availability</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
                Check if a room is available for a consultation or meeting.
            </p>
            <Button onClick={() => navigate('/rooms')} variant="secondary" className="w-full">
                Find a Room
            </Button>
        </Card>
      </div>
    </motion.div>

    <UpdateStatusModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        currentStatus={user?.status}
    />
    </>
  );
};

export default LecturerDashboard;
