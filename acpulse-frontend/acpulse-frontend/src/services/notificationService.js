import api from './api';

const notificationService = {
  // Get all notifications
  getNotifications: async () => {
    const response = await api.get('/notifications');
    return response.data;
  },

  // Get unread count
  getUnreadCount: async () => {
    const response = await api.get('/notifications/unread-count');
    return response.data;
  },

  markAllAsRead: async () => {
    const response = await api.put('/notifications/read/all');
    return response.data;
  },
};

export default notificationService;