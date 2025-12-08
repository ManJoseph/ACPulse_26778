import api from './api';

const adminService = {
  // Get dashboard statistics
  getDashboardStats: async () => {
    const response = await api.get('/admin/stats');
    return response.data;
  },

  // Get verification requests
  getVerificationRequests: async (status = 'PENDING') => {
    const response = await api.get(`/admin/verification-requests?status=${status}`);
    return response.data;
  },

  // Approve user
  approveUser: async (requestId) => {
    const response = await api.post(`/admin/verification-requests/${requestId}/approve`);
    return response.data;
  },

  // Reject user
  rejectUser: async (requestId, reason) => {
    const response = await api.post(`/admin/verification-requests/${requestId}/reject`, {
      reason,
    });
    return response.data;
  },

  // Get all users
  getUsers: async (filters = {}) => {
    const params = new URLSearchParams(filters);
    const response = await api.get(`/admin/users?${params.toString()}`);
    return response.data;
  },

  // Update a user
  updateUser: async (userId, userData) => {
    const response = await api.put(`/admin/users/${userId}`, userData);
    return response.data;
  },

  // Delete a user
  deleteUser: async (userId) => {
    const response = await api.delete(`/admin/users/${userId}`);
    return response.data;
  },
};

export default adminService;