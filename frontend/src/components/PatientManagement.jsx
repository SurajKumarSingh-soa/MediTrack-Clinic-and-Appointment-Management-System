import React, { useState, useEffect } from 'react';
import { Search, Plus, Trash2, X, AlertTriangle, FileText, Phone, Calendar } from 'lucide-react';
import { api } from '../services/api';

export default function PatientManagement({ user }) {
  const [patients, setPatients] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [showApptModal, setShowApptModal] = useState(false);
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    name: '',
    age: '28',
    contact: '',
    medicalHistory: 'None',
    allergies: '',
  });

  const [apptData, setApptData] = useState({
    date: new Date().toISOString().slice(0, 10) + ' 10:00',
    notes: 'Follow-up Consultation',
  });

  const isAdmin = user?.role === 'ADMIN';
  const isDoctor = user?.role === 'DOCTOR';

  useEffect(() => {
    loadPatients();
  }, [search]);

  const loadPatients = async () => {
    setLoading(true);
    try {
      const data = await api.getPatients(search);
      setPatients(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddPatient = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.name.trim()) return setError('Name is required');
    if (formData.contact.length !== 10) return setError('Contact must be exactly 10 digits');

    try {
      await api.addPatient({
        name: formData.name,
        age: parseInt(formData.age),
        contact: formData.contact,
        medicalHistory: formData.medicalHistory,
        allergies: formData.allergies,
      });
      setShowModal(false);
      setFormData({ name: '', age: '28', contact: '', medicalHistory: 'None', allergies: '' });
      loadPatients();
    } catch (err) {
      setError(err.message || 'Failed to add patient');
    }
  };

  const handleBookNextAppt = async (e) => {
    e.preventDefault();
    setError('');

    if (!selectedPatient) return;

    try {
      // Find doctor ID for logged-in doctor, or fallback
      let doctorId = user.id;
      if (!doctorId || doctorId === 0) {
        const docs = await api.getDoctors();
        const matched = docs.find((d) => d.name.toLowerCase().includes(user.name.toLowerCase()));
        if (matched) doctorId = matched.id;
        else if (docs.length > 0) doctorId = docs[0].id;
      }

      await api.bookAppointment({
        patientId: selectedPatient.id,
        doctorId: doctorId || 1001,
        date: apptData.date,
        notes: apptData.notes,
      });

      setShowApptModal(false);
      setSelectedPatient(null);
      alert(`Follow-up appointment booked successfully for ${selectedPatient.name}!`);
    } catch (err) {
      setError(err.message || 'Failed to schedule appointment');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to remove this patient?')) return;
    try {
      await api.deletePatient(id);
      loadPatients();
    } catch (err) {
      alert('Error deleting patient: ' + err.message);
    }
  };

  return (
    <div className="animate-fade-in">
      {/* Search & Add Bar */}
      <div style={{ display: 'flex', gap: '1rem', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div style={{ position: 'relative', flex: 1, maxWidth: '400px' }}>
          <Search size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <input
            type="text"
            className="form-control"
            style={{ paddingLeft: '2.5rem', width: '100%' }}
            placeholder="Search patients by name, contact, history..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        {/* ONLY ADMIN CAN MANUALLY REGISTER PATIENTS IN DIRECTORY */}
        {isAdmin && (
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            <Plus size={16} />
            <span>Register New Patient</span>
          </button>
        )}
      </div>

      {/* Patients Grid */}
      {loading ? (
        <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading patients...</div>
      ) : patients.length === 0 ? (
        <div className="glass-card" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
          No registered patients found.
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.5rem' }}>
          {patients.map((pat) => (
            <div key={pat.id} className="glass-card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                  <div>
                    <h3 style={{ fontSize: '1.15rem', fontWeight: 700 }}>{pat.name}</h3>
                    <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>ID: #{pat.id} • Age: {pat.age} yrs</p>
                  </div>

                  {/* ONLY ADMIN CAN DELETE PATIENTS */}
                  {isAdmin && (
                    <button 
                      className="btn btn-danger btn-sm" 
                      style={{ padding: '0.35rem 0.6rem' }} 
                      onClick={() => handleDelete(pat.id)}
                      title="Delete patient"
                    >
                      <Trash2 size={14} />
                    </button>
                  )}
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <Phone size={14} style={{ color: 'var(--accent-primary)' }} />
                    <span>+91 {pat.contact}</span>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: '0.5rem' }}>
                    <FileText size={14} style={{ color: 'var(--accent-primary)', marginTop: '3px' }} />
                    <span><strong>History:</strong> {pat.medicalHistory || 'None'}</span>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: '0.5rem' }}>
                    <AlertTriangle size={14} style={{ color: '#f59e0b', marginTop: '3px' }} />
                    <div>
                      <strong style={{ fontSize: '0.8rem' }}>Allergies:</strong>
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.35rem', marginTop: '0.35rem' }}>
                        {pat.allergies && pat.allergies.length > 0 ? (
                          pat.allergies.map((allergy, i) => (
                            <span key={i} className="badge badge-pending" style={{ fontSize: '0.7rem' }}>
                              {allergy}
                            </span>
                          ))
                        ) : (
                          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>No known allergies</span>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* DOCTOR ACTION: SCHEDULE NEXT APPOINTMENT FOR THIS PATIENT */}
              {isDoctor && (
                <div style={{ marginTop: '1.25rem', paddingTop: '1rem', borderTop: '1px solid var(--border-color)' }}>
                  <button
                    className="btn btn-primary btn-sm"
                    style={{ width: '100%' }}
                    onClick={() => {
                      setSelectedPatient(pat);
                      setShowApptModal(true);
                    }}
                  >
                    <Calendar size={14} />
                    <span>Schedule Next Visit</span>
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Doctor Schedule Follow-up Visit Modal */}
      {showApptModal && selectedPatient && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Schedule Follow-up Visit</h2>
              <button className="modal-close" onClick={() => setShowApptModal(false)}><X size={20} /></button>
            </div>

            {error && <div className="status-pill btn-danger" style={{ marginBottom: '1rem' }}>{error}</div>}

            <form onSubmit={handleBookNextAppt}>
              <div className="form-group">
                <label>Selected Patient</label>
                <input
                  type="text"
                  className="form-control"
                  value={`${selectedPatient.name} (ID: #${selectedPatient.id})`}
                  disabled
                />
              </div>

              <div className="form-group">
                <label>Attending Doctor</label>
                <input
                  type="text"
                  className="form-control"
                  value={`${user.name} (My Consultation)`}
                  disabled
                />
              </div>

              <div className="form-group">
                <label>Follow-up Date & Time (YYYY-MM-DD HH:mm)</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. 2026-08-01 10:00"
                  value={apptData.date}
                  onChange={(e) => setApptData({ ...apptData, date: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Consultation Notes / Instructions</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Follow-up checkup for lab test results"
                  value={apptData.notes}
                  onChange={(e) => setApptData({ ...apptData, notes: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowApptModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Confirm Booking</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Patient Modal (Admin Only) */}
      {showModal && isAdmin && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Register New Patient</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}><X size={20} /></button>
            </div>

            {error && <div className="status-pill btn-danger" style={{ marginBottom: '1rem' }}>{error}</div>}

            <form onSubmit={handleAddPatient}>
              <div className="form-group">
                <label>Patient Full Name</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Suraj Kumar"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label>Age</label>
                  <input
                    type="number"
                    className="form-control"
                    value={formData.age}
                    onChange={(e) => setFormData({ ...formData, age: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Contact Number (10 digits)</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="9123456789"
                    maxLength={10}
                    value={formData.contact}
                    onChange={(e) => setFormData({ ...formData, contact: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Medical History</label>
                <textarea
                  className="form-control"
                  rows={2}
                  placeholder="e.g. Hypertension, Seasonal asthma"
                  value={formData.medicalHistory}
                  onChange={(e) => setFormData({ ...formData, medicalHistory: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>Known Allergies (Comma separated)</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Penicillin, Peanuts, Pollen"
                  value={formData.allergies}
                  onChange={(e) => setFormData({ ...formData, allergies: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Register Patient</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
