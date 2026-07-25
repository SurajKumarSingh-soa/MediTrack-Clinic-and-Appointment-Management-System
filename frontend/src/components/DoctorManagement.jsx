import React, { useState, useEffect } from 'react';
import { Stethoscope, Search, Plus, Trash2, X, Award, Phone, Calendar } from 'lucide-react';
import { api } from '../services/api';

export default function DoctorManagement({ user, setActiveTab }) {
  const [doctors, setDoctors] = useState([]);
  const [search, setSearch] = useState('');
  const [selectedSpec, setSelectedSpec] = useState('');
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    name: '',
    age: '35',
    contact: '',
    specialization: 'GENERAL',
    consultationFee: '500',
    qualification: 'MBBS',
  });

  const cardColors = ['card-cyan', 'card-purple', 'card-emerald', 'card-rose', 'card-amber'];

  const specializations = [
    'GENERAL',
    'CARDIOLOGY',
    'DERMATOLOGY',
    'NEUROLOGY',
    'ORTHOPEDICS',
    'PEDIATRICS',
    'GYNECOLOGY',
    'ENT',
  ];

  useEffect(() => {
    loadDoctors();
  }, [search, selectedSpec]);

  const loadDoctors = async () => {
    setLoading(true);
    try {
      let data = await api.getDoctors(search);
      if (selectedSpec) {
        data = data.filter((d) => d.specialization === selectedSpec);
      }
      setDoctors(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddDoctor = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.name.trim()) return setError('Name is required');
    if (formData.contact.length !== 10) return setError('Contact must be exactly 10 digits');

    try {
      await api.addDoctor({
        name: formData.name,
        age: parseInt(formData.age),
        contact: formData.contact,
        specialization: formData.specialization,
        consultationFee: parseFloat(formData.consultationFee),
        qualification: formData.qualification,
      });
      setShowModal(false);
      setFormData({ name: '', age: '35', contact: '', specialization: 'GENERAL', consultationFee: '500', qualification: 'MBBS' });
      loadDoctors();
    } catch (err) {
      setError(err.message || 'Failed to add doctor');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to remove this doctor?')) return;
    try {
      await api.deleteDoctor(id);
      loadDoctors();
    } catch (err) {
      alert('Error deleting doctor: ' + err.message);
    }
  };

  const isManager = user?.role === 'ADMIN';

  return (
    <div className="animate-fade-in">
      {/* Controls Bar */}
      <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', gap: '1rem', flex: 1, minWidth: '300px' }}>
          <div style={{ position: 'relative', flex: 1 }}>
            <Search size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              type="text"
              className="form-control"
              style={{ paddingLeft: '2.5rem', width: '100%' }}
              placeholder="Search doctors by name, qualification..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <select
            className="form-control"
            style={{ width: '200px' }}
            value={selectedSpec}
            onChange={(e) => setSelectedSpec(e.target.value)}
          >
            <option value="">All Specializations</option>
            {specializations.map((spec) => (
              <option key={spec} value={spec}>{spec}</option>
            ))}
          </select>
        </div>

        {/* ONLY ADMIN / CLINIC MANAGERS CAN ADD DOCTORS */}
        {isManager && (
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            <Plus size={16} />
            <span>Add New Doctor</span>
          </button>
        )}
      </div>

      {/* Doctor Cards Grid with Vibrant Multi-Colors */}
      {loading ? (
        <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading doctors...</div>
      ) : doctors.length === 0 ? (
        <div className="glass-card" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
          No doctors found matching criteria.
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.5rem' }}>
          {doctors.map((doc, idx) => {
            const cardColorClass = cardColors[idx % cardColors.length];
            return (
              <div key={doc.id} className={`glass-card ${cardColorClass}`} style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                    <div>
                      <span className="badge badge-spec" style={{ marginBottom: '0.5rem' }}>{doc.specialization}</span>
                      <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--text-primary)' }}>{doc.name}</h3>
                      <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>ID: #{doc.id} • Age: {doc.age} yrs</p>
                    </div>

                    {isManager && (
                      <button 
                        className="btn btn-danger btn-sm" 
                        style={{ padding: '0.35rem 0.6rem' }} 
                        onClick={() => handleDelete(doc.id)}
                        title="Delete doctor"
                      >
                        <Trash2 size={14} />
                      </button>
                    )}
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <Award size={14} style={{ color: '#c084fc' }} />
                      <span>{doc.qualification}</span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <Phone size={14} style={{ color: '#38bdf8' }} />
                      <span>+91 {doc.contact}</span>
                    </div>
                  </div>
                </div>

                <div style={{ marginTop: '1.25rem', paddingTop: '1rem', borderTop: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>Consultation Fee: </span>
                    <div style={{ fontSize: '1.2rem', fontWeight: 800, background: 'var(--gradient-cyan)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>₹{doc.consultationFee.toFixed(2)}</div>
                  </div>

                  {user?.role === 'PATIENT' && (
                    <button className="btn btn-primary btn-sm" onClick={() => setActiveTab && setActiveTab('appointments')}>
                      <Calendar size={14} />
                      <span>Book Visit</span>
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Add Doctor Modal (Admin Only) */}
      {showModal && isManager && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Add New Doctor</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}><X size={20} /></button>
            </div>

            {error && <div className="status-pill btn-danger" style={{ marginBottom: '1rem' }}>{error}</div>}

            <form onSubmit={handleAddDoctor}>
              <div className="form-group">
                <label>Doctor Full Name</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Dr. Ankit Kishore"
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
                    placeholder="9876543210"
                    maxLength={10}
                    value={formData.contact}
                    onChange={(e) => setFormData({ ...formData, contact: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label>Specialization</label>
                  <select
                    className="form-control"
                    value={formData.specialization}
                    onChange={(e) => setFormData({ ...formData, specialization: e.target.value })}
                  >
                    {specializations.map((spec) => (
                      <option key={spec} value={spec}>{spec}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Consultation Fee (₹)</label>
                  <input
                    type="number"
                    className="form-control"
                    value={formData.consultationFee}
                    onChange={(e) => setFormData({ ...formData, consultationFee: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Qualification</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. MBBS, MD (Cardiology)"
                  value={formData.qualification}
                  onChange={(e) => setFormData({ ...formData, qualification: e.target.value })}
                  required
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Add Doctor</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
