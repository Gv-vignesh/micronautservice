import axios from 'axios';

const API_URL = 'http://localhost:8080';

const axiosInstance = axios.create({
    baseURL: API_URL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Add a response interceptor to handle 401 errors
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Clear local storage and redirect to login
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const authService = {
    register: async (username, password) => {
        try {
            const response = await axiosInstance.post('/auth/register', {
                username,
                password
            });
            return response.data;
        } catch (error) {
            throw error.response?.data?.message || 'Registration failed';
        }
    },

    login: async (username, password) => {
        try {
            const response = await axiosInstance.post('/auth/login', {
                username,
                password
            });
            const token = response.data.token;
            if (token) {
                localStorage.setItem('token', token);
                // Set the token for future requests
                axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            }
            return response.data;
        } catch (error) {
            throw error.response?.data?.message || 'Login failed';
        }
    },

    logout: () => {
        localStorage.removeItem('token');
        // Remove the token from future requests
        delete axiosInstance.defaults.headers.common['Authorization'];
    },

    getCurrentToken: () => {
        return localStorage.getItem('token');
    },

    // Add this new method to call the /hello endpoint
    getGreeting: async () => {
        try {
            const token = localStorage.getItem('token');
            console.log('Token from localStorage:', token);
            if (token) {
                axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            }
            const response = await axiosInstance.get('/hello');
            return response;
        } catch (error) {
            throw error.response?.data?.message || 'Failed to get greeting';
        }
    }
}; 