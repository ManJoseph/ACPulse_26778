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

    const apiUrl = `/lecturers?${params.toString()}`;
    console.log('LecturerService: Fetching lecturers from:', apiUrl);

    try {
      const response = await api.get(apiUrl);
      console.log('LecturerService: API response received:', response.data);
      return response.data;
    } catch (error) {
      console.error('LecturerService: Error fetching lecturers:', error);
      throw error; // Re-throw to be caught by the component
    }
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

  // Schedule management
  getLecturerSchedule: async (lecturerId) => {
    const response = await api.get(`/lecturers/${lecturerId}/schedule`);
    return response.data;
  },

  setLecturerSchedule: async (lecturerId, scheduleData) => {
    const response = await api.post(`/lecturers/${lecturerId}/schedule`, scheduleData);
    return response.data;
  },

  updateLecturerSchedule: async (scheduleId, scheduleData) => {
    const response = await api.post(`/lecturers/${scheduleData.lecturerId}/schedule`, { ...scheduleData, id: scheduleId }); // Re-use POST for update, assuming backend handles ID for update
    return response.data;
  },

  deleteLecturerScheduleEntry: async (scheduleId) => {
    const response = await api.delete(`/lecturers/schedule/${scheduleId}`);
    return response.data;
  },
};

export default lecturerService;