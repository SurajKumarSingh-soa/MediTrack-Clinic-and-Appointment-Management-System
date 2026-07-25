// MediTrack REST API Client

const API_BASE_URL = 'http://localhost:8080/api';

async function request(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`;
  const defaultHeaders = {
    'Content-Type': 'application/json',
  };

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
  };

  try {
    const response = await fetch(url, config);
    if (!response.ok) {
      const errData = await response.json().catch(() => ({}));
      throw new Error(errData.error || `HTTP error ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`API Request Error [${endpoint}]:`, error);
    throw error;
  }
}

export const api = {
  // Doctors
  getDoctors: (query = '') => request(`/doctors${query ? `?q=${encodeURIComponent(query)}` : ''}`),
  addDoctor: (data) => request('/doctors', { method: 'POST', body: JSON.stringify(data) }),
  updateDoctor: (data) => request('/doctors', { method: 'PUT', body: JSON.stringify(data) }),
  deleteDoctor: (id) => request(`/doctors?id=${id}`, { method: 'DELETE' }),

  // Patients
  getPatients: (query = '') => request(`/patients${query ? `?q=${encodeURIComponent(query)}` : ''}`),
  addPatient: (data) => request('/patients', { method: 'POST', body: JSON.stringify(data) }),
  updatePatient: (data) => request('/patients', { method: 'PUT', body: JSON.stringify(data) }),
  deletePatient: (id) => request(`/patients?id=${id}`, { method: 'DELETE' }),

  // Appointments
  getAppointments: () => request('/appointments'),
  bookAppointment: (data) => request('/appointments', { method: 'POST', body: JSON.stringify(data) }),
  completeAppointment: (id) => request(`/appointments/complete?id=${id}`, { method: 'PUT' }),
  cancelAppointment: (id) => request(`/appointments/cancel?id=${id}`, { method: 'PUT' }),

  // Billing
  generateBill: (data) => request('/billing', { method: 'POST', body: JSON.stringify(data) }),

  // AI Recommendation Engine
  getAIRecommendation: (symptoms) => request('/ai', { method: 'POST', body: JSON.stringify({ symptoms }) }),

  // Analytics & Streams
  getAnalytics: () => request('/analytics'),

  // File Persistence CSV Sync
  saveData: () => request('/data/save', { method: 'POST' }),
  loadData: () => request('/data/load', { method: 'POST' }),
};
