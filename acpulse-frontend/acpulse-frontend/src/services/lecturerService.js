import api from './api';

const lecturerService = {
  /**
   * Fetches a list of lecturers with optional filters.
   * @param {object} filters - Filters for the lecturer list.
   * @param {string} [filters.searchTerm] - Search term for lecturer name or department.
   * @param {string} [filters.status] - Filter by lecturer status.
   * @param {number} [filters.page] - Page number (0-indexed).
   * @param {number} [filters.size] - Number of items per page.
   * @returns {Promise<object>} Paged list of lecturers.
   */
  getLecturers: async (filters = {}) => {
    const { searchTerm, status, page, size } = filters;
    const params = new URLSearchParams();
    if (searchTerm) params.append('search', searchTerm);
    if (status) params.append('status', status);
    if (page !== undefined) params.append('page', page);
    if (size) params.append('size', size);

    const response = await api.get(`/lecturers?${params.toString()}`);
    return response.data;
  },

  getLecturerById: async (lecturerId) => {
    const response = await api.get(`/lecturers/${lecturerId}`);
    return response.data;
  },

  // Get lecturer status
  getStatus: async () => {
    const response = await api.get('/lecturers/status');
    return response.data;
  },

  // Update status
  updateStatus: async (data) => {
    const response = await api.put('/lecturers/status', data);
    return response.data;
  },
};

export default lecturerService;