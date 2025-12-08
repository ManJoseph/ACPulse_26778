import api from './api';

/**
 * Authentication service for handling user login, registration, and logout.
 */
const authService = {
  /**
   * Logs in a user.
   * @param {object} credentials - The user's login credentials.
   * @param {string} credentials.email - The user's email.
   * @param {string} credentials.password - The user's password.
   * @returns {Promise<object>} The response data, including the token and user info.
   */
  login: async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials);
      return response.data;
    } catch (error) {
      throw error.response.data || error;
    }
  },

  /**
   * Registers a new user.
   * @param {object} userData - The data for the new user.
   * @returns {Promise<object>} The response data from the server.
   */
  register: async (userData) => {
    try {
      const response = await api.post('/auth/register', userData);
      return response.data;
    } catch (error) {
      throw error.response.data || error;
    }
  },

  /**
   * Sends a password reset request.
   * @param {string} email - The user's email address.
   * @returns {Promise<object>} The response data from the server.
   */
  forgotPassword: async (email) => {
    try {
      // Note: The backend endpoint for this needs to be created.
      // This is a hypothetical implementation.
      const response = await api.post('/auth/forgot-password', { email });
      return response.data;
    } catch (error) {
      throw error.response.data || error;
    }
  },

  /**
   * Resets the user's password using a token.
   * @param {string} token - The password reset token from the email.
   * @param {string} newPassword - The new password.
   * @returns {Promise<object>} The response data from the server.
   */
  resetPassword: async (token, newPassword) => {
    try {
      // Note: The backend endpoint for this needs to be created.
      const response = await api.post('/auth/reset-password', { token, newPassword });
      return response.data;
    } catch (error) {
      throw error.response.data || error;
    }
  },

  /**
   * Fetches the current user's profile information.
   * Useful for re-validating a session on app load.
   * @returns {Promise<object>} The user's profile data.
   */
  getProfile: async () => {
    try {
      // This endpoint might need to be created on the backend (e.g., GET /api/users/me)
      const response = await api.get('/profile');
      return response.data;
    } catch (error) {
      throw error.response.data || error;
    }
  },
};

export default authService;
