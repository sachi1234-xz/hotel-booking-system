import api from './api';

export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  validate: () => api.get('/auth/validate'),
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
