import api from './api';
import { useAuthStore } from '../store/authStore';

const getUserId = () => useAuthStore.getState().user?.userId;

const notificationService = {
  // Get all notifications for current user
  getNotifications: async () => {
    const userId = getUserId();
    if (!userId) throw new Error('User not authenticated');
    const response = await api.get(`/notifications?userId=${userId}`);
    return response.data;
  },

  // Get unread count for current user
  getUnreadCount: async () => {
    const userId = getUserId();
    if (!userId) throw new Error('User not authenticated');
    const response = await api.get(`/notifications/unread-count?userId=${userId}`);
    return response.data;
  },

  // Mark all as read for current user
  markAllAsRead: async () => {
    const userId = getUserId();
    if (!userId) throw new Error('User not authenticated');
    const response = await api.put(`/notifications/read/all?userId=${userId}`);
    return response.data;
  },

  // Mark a single notification as read
  markAsRead: async (notificationId) => {
    const userId = getUserId();
    if (!userId) throw new Error('User not authenticated');
    const response = await api.put(`/notifications/${notificationId}/read?userId=${userId}`);
    return response.data;
  }
};

export default notificationService;