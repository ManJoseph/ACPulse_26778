import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// Get the initial user data from localStorage if it exists
const initialUser = JSON.parse(localStorage.getItem('user')) || null;
const initialToken = localStorage.getItem('token') || null;

export const useAuthStore = create(
  persist(
    (set) => ({
      user: initialUser,
      token: initialToken,
      isAuthenticated: !!initialToken,

      // --- ACTIONS ---

      // Set user and token after successful login
      login: (userData, token) => {
        // Also update the plain localStorage for immediate access if needed
        localStorage.setItem('user', JSON.stringify(userData));
        localStorage.setItem('token', token);

        set({
          user: userData,
          token: token,
          isAuthenticated: true,
        });
      },

      // Clear user and token on logout
      logout: () => {
        // Clear localStorage
        localStorage.removeItem('user');
        localStorage.removeItem('token');

        set({
          user: null,
          token: null,
          isAuthenticated: false,
        });
      },

      // Update user information (e.g., after profile update)
      setUser: (userData) => {
        localStorage.setItem('user', JSON.stringify(userData));
        set({ user: userData });
      },
    }),
    {
      name: 'auth-storage', // name of the item in the storage (must be unique)
      getStorage: () => localStorage, // (optional) by default, 'localStorage' is used
    }
  )
);

// Selector to get the current user
export const useCurrentUser = () => useAuthStore((state) => state.user);

// Selector to get the authentication status
export const useIsAuthenticated = () => useAuthStore((state) => state.isAuthenticated);

export default useAuthStore;
