import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-hot-toast';
import { CalendarDays, Plus, Trash2, Save } from 'lucide-react';
import { format, parseISO } from 'date-fns';

import Card from '../components/common/Card';
import Button from '../components/common/Button';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import Input from '../components/common/Input';
import Select from '../components/common/Select';
import { useAuthStore } from '../store/authStore';
import { lecturerService } from '../services/lecturerService'; // Assuming this service will handle schedule API
import { LECTURER_STATUS } from '../utils/constants'; // For possible schedule related statuses

// Helper to format time for display/input
const formatTime = (timeString) => {
  if (!timeString) return '';
  // Assuming timeString is in "HH:mm" format or similar
  // Adjust as per backend time format
  return timeString;
};

// Represents a single schedule entry (e.g., a time slot for a day)
const ScheduleEntry = ({ day, time, onEdit, onDelete }) => (
  <div className="flex justify-between items-center p-2 border-b last:border-b-0 dark:border-dark-700">
    <span>{day}: {time}</span>
    <div className="flex gap-2">
      <Button variant="ghost" size="sm" onClick={onEdit}>
        <Edit className="w-4 h-4" />
      </Button>
      <Button variant="danger" size="sm" onClick={onDelete}>
        <Trash2 className="w-4 h-4" />
      </Button>
    </div>
  </div>
);

const LecturerSchedule = () => {
  const queryClient = useQueryClient();
  const { user } = useAuthStore();
  const lecturerId = user?.userId;
  const { register, handleSubmit, reset, setValue } = useForm();
  const [isEditing, setIsEditing] = useState(false);
  const [editingScheduleId, setEditingScheduleId] = useState(null);

  // Fetch lecturer's schedule
  const { data: schedule, isLoading, error } = useQuery({
    queryKey: ['lecturerSchedule', lecturerId],
    queryFn: () => lecturerService.getLecturerSchedule(lecturerId), // Assuming this API exists
    enabled: !!lecturerId,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });

  // Mutation for setting/updating schedule
  const saveScheduleMutation = useMutation({
    mutationFn: (newSchedule) => {
      if (isEditing) {
        return lecturerService.updateLecturerSchedule(editingScheduleId, newSchedule); // Assuming API for update
      }
      return lecturerService.setLecturerSchedule(lecturerId, newSchedule); // Assuming API for create
    },
    onSuccess: () => {
      toast.success(`Schedule ${isEditing ? 'updated' : 'set'} successfully!`);
      queryClient.invalidateQueries(['lecturerSchedule', lecturerId]);
      reset(); // Clear form
      setIsEditing(false);
      setEditingScheduleId(null);
    },
    onError: (err) => {
      toast.error(err.message || 'Failed to save schedule.');
    },
  });

  // Mutation for deleting a schedule entry
  const deleteScheduleMutation = useMutation({
    mutationFn: (scheduleId) => lecturerService.deleteLecturerScheduleEntry(scheduleId), // Assuming API for delete
    onSuccess: () => {
      toast.success('Schedule entry deleted successfully!');
      queryClient.invalidateQueries(['lecturerSchedule', lecturerId]);
    },
    onError: (err) => {
      toast.error(err.message || 'Failed to delete schedule entry.');
    },
  });

  // Pre-fill form for editing
  const handleEdit = (entry) => {
    setValue('day', entry.day);
    setValue('startTime', entry.startTime);
    setValue('endTime', entry.endTime);
    setIsEditing(true);
    setEditingScheduleId(entry.id);
  };

  const onSubmit = (data) => {
    saveScheduleMutation.mutate(data);
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <EmptyState
        icon={CalendarDays}
        title="Error Loading Schedule"
        message={error.message || "Could not fetch your schedule."}
      />
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="space-y-6"
    >
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold tracking-tight">My Schedule</h1>
      </div>

      <Card>
        <Card.Header>
          <Card.Title>{isEditing ? 'Edit Schedule Entry' : 'Add New Schedule Entry'}</Card.Title>
        </Card.Header>
        <Card.Body>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Select label="Day" id="day" {...register('day', { required: 'Day is required' })}>
              <option value="">Select Day</option>
              {['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'].map(day => (
                <option key={day} value={day}>{day}</option>
              ))}
            </Select>
            <Input
              label="Start Time"
              id="startTime"
              type="time"
              {...register('startTime', { required: 'Start time is required' })}
            />
            <Input
              label="End Time"
              id="endTime"
              type="time"
              {...register('endTime', { required: 'End time is required' })}
            />
            <div className="flex justify-end gap-2">
              {isEditing && (
                <Button variant="outline" onClick={() => { reset(); setIsEditing(false); setEditingScheduleId(null); }}>
                  Cancel Edit
                </Button>
              )}
              <Button type="submit" loading={saveScheduleMutation.isLoading}>
                <Save className="w-4 h-4 mr-2" /> {isEditing ? 'Update Entry' : 'Add Entry'}
              </Button>
            </div>
          </form>
        </Card.Body>
      </Card>

      <Card>
        <Card.Header>
          <Card.Title>Current Schedule</Card.Title>
        </Card.Header>
        <Card.Body>
          {schedule?.length === 0 ? (
            <EmptyState
              icon={CalendarDays}
              title="No Schedule Set"
              message="Your schedule is currently empty. Add new entries above."
            />
          ) : (
            <div>
              {schedule?.map((entry) => (
                <ScheduleEntry
                  key={entry.id}
                  day={entry.day}
                  time={`${formatTime(entry.startTime)} - ${formatTime(entry.endTime)}`}
                  onEdit={() => handleEdit(entry)}
                  onDelete={() => deleteScheduleMutation.mutate(entry.id)}
                />
              ))}
            </div>
          )}
        </Card.Body>
      </Card>
    </motion.div>
  );
};

export default LecturerSchedule;