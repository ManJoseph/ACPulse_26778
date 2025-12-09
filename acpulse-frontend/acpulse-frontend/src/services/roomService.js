import api from './api';

const roomService = {
  // Search room by number
  searchRoom: async (roomNumber) => {
    const response = await api.get(`/rooms/search?roomNumber=${roomNumber}`);
    return response.data;
  },

  // Get all rooms (backend returns a simple array)
  getRooms: async (filters = {}) => {
    const { searchTerm, status } = filters;
    const params = new URLSearchParams();
    if (searchTerm) params.append('search', searchTerm);
    if (status) params.append('status', status);

    // Backend currently returns a plain list, not paginated
    const response = await api.get(`/rooms?${params.toString()}`);
    return response.data;
  },

  getRoomById: async (roomId) => {
    const response = await api.get(`/rooms/${roomId}`);
    return response.data;
  },

  // Occupy room (Lecturer only)
  occupyRoom: async (data) => {
    const response = await api.post('/lecturer/occupy-room', data);
    return response.data;
  },

  // Extend room occupation
  extendRoom: async (roomId, data) => {
    const response = await api.put(`/lecturer/extend-room/${roomId}`, data);
    return response.data;
  },

  // Release room
  releaseRoom: async (roomId) => {
    const response = await api.post(`/lecturer/release-room/${roomId}`);
    return response.data;
  },
};

export default roomService;