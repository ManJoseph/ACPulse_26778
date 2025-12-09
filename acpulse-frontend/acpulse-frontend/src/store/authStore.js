import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set) => ({
      // The initial state should be empty.
      // `persist` middleware will rehydrate it from localStorage on load.
      user: null,
      token: null,
      isAuthenticated: false,

      // --- ACTIONS ---

      /**
       * Sets user and token after successful login.
       * The `authResponse` is the data object received from the backend API.
       * @param {object} authResponse - The response from the /api/auth/login endpoint.
       */
      login: (authResponse) => {
        const { token, ...userData } = authResponse;
        // Store token in both state and localStorage for redundancy
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(userData));
        set({
          user: userData,
          token: token,
          isAuthenticated: true,
        });
      },

      // Clear user and token on logout
      logout: () => {
        // Clear localStorage
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        // Just update the state. The `persist` middleware will clear the storage.
        set({
          user: null,
          token: null,
          isAuthenticated: false,
        });
      },

      // Update user information (e.g., after profile update)
      setUser: (userData) => {
        localStorage.setItem('user', JSON.stringify(userData));
        set((state) => ({
          user: { ...state.user, ...userData },
        }));
      },
    }),
    {
      name: 'auth-storage', // name of the item in the storage (must be unique)
      getStorage: () => localStorage, // explicitly use localStorage
    }
  )
);

// Selector to get the current user
export const useCurrentUser = () => useAuthStore((state) => state.user);

// Selector to get the authentication status
export const useIsAuthenticated = () => useAuthStore((state) => state.isAuthenticated);

export default useAuthStore;

