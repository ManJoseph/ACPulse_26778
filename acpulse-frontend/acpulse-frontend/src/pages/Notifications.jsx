import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import notificationService from '../services/notificationService';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import { Bell, Archive } from 'lucide-react';
import { formatRelativeTime } from '../utils/helpers';
import Button from '../components/common/Button';
import { toast } from 'react-hot-toast';

const NotificationItem = ({ notification, onMarkAsRead }) => (
    <div className={`p-4 flex items-start gap-4 border-b dark:border-dark-700 ${!notification.read ? 'bg-primary-50 dark:bg-primary-900/10' : ''}`}>
        <div className="w-8 h-8 bg-primary-100 dark:bg-primary-900/20 rounded-full flex items-center justify-center flex-shrink-0">
            <Bell className="w-5 h-5 text-primary-600 dark:text-primary-400" />
        </div>
        <div className="flex-1">
            <p className="text-gray-800 dark:text-gray-200">{notification.message}</p>
            <span className="text-sm text-gray-500 dark:text-gray-400">
                {formatRelativeTime(notification.createdAt)}
            </span>
        </div>
        {!notification.read && (
            <Button size="sm" variant="ghost" onClick={() => onMarkAsRead(notification.id)}>
                Mark as read
            </Button>
        )}
    </div>
);


const Notifications = () => {
    const queryClient = useQueryClient();
    const { data: notifications, isLoading, error } = useQuery(['notifications'], notificationService.getNotifications);

    const markAsReadMutation = useMutation(notificationService.markAsRead, {
        onSuccess: () => {
            queryClient.invalidateQueries('notifications');
        }
    });

    const markAllAsReadMutation = useMutation(notificationService.markAllAsRead, {
        onSuccess: () => {
            queryClient.invalidateQueries('notifications');
            toast.success('All notifications marked as read.');
        }
    });

    const handleMarkAsRead = (id) => {
        markAsReadMutation.mutate(id);
    }

    const handleMarkAllAsRead = () => {
        markAllAsReadMutation.mutate();
    }

  return (
    <div className="bg-white dark:bg-dark-800 rounded-lg shadow-md">
       <div className="p-4 border-b dark:border-dark-700 flex justify-between items-center">
        <h1 className="text-xl font-bold">Notifications</h1>
        <Button 
            variant="outline" 
            onClick={handleMarkAllAsRead}
            disabled={markAllAsReadMutation.isLoading || !notifications?.some(n => !n.read)}
        >
            Mark All as Read
        </Button>
       </div>

       {isLoading && <div className="p-8 flex justify-center"><LoadingSpinner /></div>}
       {error && <EmptyState title="Error" message={error.message} icon={<Bell />} />}
       {!isLoading && !error && notifications?.length === 0 && (
           <EmptyState title="No Notifications" message="You're all caught up!" icon={<Archive />} />
       )}

       <div>
        {notifications?.map(notification => (
            <NotificationItem key={notification.id} notification={notification} onMarkAsRead={handleMarkAsRead} />
        ))}
       </div>
    </div>
  );
};

export default Notifications;
