import api from './api';

export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  validate: () => api.get('/auth/validate'),
  updateProfile: (data) => api.put('/auth/profile', data),
  changePassword: (data) => api.put('/auth/change-password', data),
  deleteAccount: () => api.delete('/auth/profile'),
  getAllUsers: () => api.get('/auth/users'),
  getUserById: (id) => api.get(`/auth/users/${id}`),
  requestPasswordReset: (data) => api.post('/auth/password-reset/request', data),
  verifyEmail: (data) => api.post('/auth/verify-email', data),
  sendVerificationCode: (email) => api.post(`/auth/verification-code/send?email=${email}`),
};

export const hotelAPI = {
  getAll: () => api.get('/hotels'),
  getById: (id) => api.get(`/hotels/${id}`),
  create: (data) => api.post('/hotels', data),
  update: (id, data) => api.put(`/hotels/${id}`, data),
  delete: (id) => api.delete(`/hotels/${id}`),
  getRooms: (hotelId) => api.get(`/hotels/${hotelId}/rooms`),
  addRoom: (hotelId, data) => api.post(`/hotels/${hotelId}/rooms`, data),
};

export const roomAPI = {
  getAvailable: () => api.get('/rooms/available'),
  update: (id, data) => api.put(`/rooms/${id}`, data),
  delete: (id) => api.delete(`/rooms/${id}`),
};

export const bookingAPI = {
  create: (data) => api.post('/bookings', data),
  getById: (id) => api.get(`/bookings/${id}`),
  getMyBookings: () => api.get('/bookings/my'),
  cancel: (id) => api.delete(`/bookings/${id}`),
};

export const paymentAPI = {
  process: (data) => api.post('/payments/process', data),
  getMyPayments: () => api.get('/payments/my'),
  getById: (id) => api.get(`/payments/${id}`),
  getInvoice: (id) => api.get(`/payments/${id}/invoice`),
};
