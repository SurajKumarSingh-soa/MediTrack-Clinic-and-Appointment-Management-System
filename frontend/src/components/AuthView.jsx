import React, { useState } from 'react';
import { Stethoscope, Users, Lock, ArrowRight, ShieldCheck, CheckCircle2, AlertCircle } from 'lucide-react';
import { api } from '../services/api';

export default function AuthView({ onLoginSuccess }) {
  const [role, setRole] = useState('PATIENT'); // 'PATIENT' | 'DOCTOR' | 'ADMIN'
  const [mode, setMode] = useState('LOGIN'); // 'LOGIN' | 'SIGNUP'
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [loading, setLoading] = useState(false);

  // Form State
  const [formData, setFormData] = useState({
    name: '',
    contact: '',
    age: '30',
    specialization: 'GENERAL',
    consultationFee: '500',
    qualification: 'MBBS',
    medicalHistory: 'None',
    allergies: '',
  });

  const specializations = [
    'GENERAL', 'CARDIOLOGY', 'DERMATOLOGY', 'NEUROLOGY',
    'ORTHOPEDICS', 'PEDIATRICS', 'GYNECOLOGY', 'ENT'
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMsg('');
    setLoading(true);

    try {
      if (role === 'ADMIN') {
        // Admin Master Login
        onLoginSuccess({
          role: 'ADMIN',
          name: 'Clinic Administrator',
          id: 0,
        });
        return;
      }

      if (mode === 'SIGNUP') {
        if (role === 'DOCTOR') {
          if (!formData.name.trim()) throw new Error('Name is required');
          if (formData.contact.length !== 10) throw new Error('Contact must be 10 digits');
          
          await api.addDoctor({
            name: formData.name,
            age: parseInt(formData.age),
            contact: formData.contact,
            specialization: formData.specialization,
            consultationFee: parseFloat(formData.consultationFee),
            qualification: formData.qualification,
          });

          // Show success message and redirect to LOGIN mode!
          setSuccessMsg(`Doctor account created for ${formData.name}! Please sign in now.`);
          setMode('LOGIN');
          setLoading(false);
          return;

        } else {
          // PATIENT SIGNUP
          if (!formData.name.trim()) throw new Error('Name is required');
          if (formData.contact.length !== 10) throw new Error('Contact must be 10 digits');

          await api.addPatient({
            name: formData.name,
            age: parseInt(formData.age),
            contact: formData.contact,
            medicalHistory: formData.medicalHistory,
            allergies: formData.allergies,
          });

          // Show success message and redirect to LOGIN mode!
          setSuccessMsg(`Patient account created for ${formData.name}! Please sign in now.`);
          setMode('LOGIN');
          setLoading(false);
          return;
        }

      } else {
        // LOGIN MODE WITH STRICT ROLE VALIDATION
        const queryText = (formData.name || formData.contact).trim().toLowerCase();
        if (!queryText) throw new Error('Please enter your name, contact, or ID');

        if (role === 'DOCTOR') {
          // Check if user exists in Doctors list
          const docs = await api.getDoctors();
          const matchedDoc = docs.find(
            (d) =>
              d.name.toLowerCase() === queryText ||
              d.name.toLowerCase().includes(queryText) ||
              d.contact === queryText ||
              d.id.toString() === queryText
          );

          if (!matchedDoc) {
            // Check if they accidentally entered a patient name into Doctor login tab
            const pats = await api.getPatients();
            const isPatient = pats.some(
              (p) => p.name.toLowerCase() === queryText || p.contact === queryText || p.id.toString() === queryText
            );

            if (isPatient) {
              throw new Error('Access Denied: This account is registered as a Patient. Please select the Patient tab to sign in.');
            } else {
              throw new Error('No Doctor found with those credentials. Please check spelling or sign up first.');
            }
          }

          // Authenticated as Doctor
          onLoginSuccess({
            role: 'DOCTOR',
            name: matchedDoc.name,
            id: matchedDoc.id,
            specialization: matchedDoc.specialization,
            fee: matchedDoc.consultationFee,
          });

        } else if (role === 'PATIENT') {
          // Check if user exists in Patients list
          const pats = await api.getPatients();
          const matchedPat = pats.find(
            (p) =>
              p.name.toLowerCase() === queryText ||
              p.name.toLowerCase().includes(queryText) ||
              p.contact === queryText ||
              p.id.toString() === queryText
          );

          if (!matchedPat) {
            // Check if they accidentally entered a doctor name into Patient login tab
            const docs = await api.getDoctors();
            const isDoctor = docs.some(
              (d) => d.name.toLowerCase() === queryText || d.contact === queryText || d.id.toString() === queryText
            );

            if (isDoctor) {
              throw new Error('Access Denied: This account is registered as a Doctor. Please select the Doctor tab to sign in.');
            } else {
              throw new Error('No Patient found with those credentials. Please check spelling or sign up first.');
            }
          }

          // Authenticated as Patient
          onLoginSuccess({
            role: 'PATIENT',
            name: matchedPat.name,
            id: matchedPat.id,
            contact: matchedPat.contact,
          });
        }
      }
    } catch (err) {
      setError(err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'var(--bg-primary)', padding: '2rem' }}>
      <div className="glass-card animate-fade-in" style={{ width: '100%', maxWidth: '480px', padding: '2.5rem' }}>
        
        {/* Brand Header */}
        <div style={{ textAlign: 'center', marginBottom: '1.75rem' }}>
          <div className="sidebar-logo-icon" style={{ margin: '0 auto 0.75rem auto', width: '48px', height: '48px' }}>
            <Stethoscope size={24} />
          </div>
          <h1 style={{ fontFamily: 'var(--font-editorial)', fontSize: '1.8rem', fontStyle: 'italic' }}>MediTrack</h1>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
            Clinic & Appointment Management System
          </p>
        </div>

        {/* Role Selector Tabs */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '0.5rem', background: 'var(--bg-subtle)', padding: '0.35rem', borderRadius: '12px', marginBottom: '1.5rem' }}>
          <button
            className={`btn btn-sm ${role === 'PATIENT' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ border: 'none' }}
            onClick={() => { setRole('PATIENT'); setError(''); setSuccessMsg(''); }}
          >
            <Users size={14} />
            <span>Patient</span>
          </button>

          <button
            className={`btn btn-sm ${role === 'DOCTOR' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ border: 'none' }}
            onClick={() => { setRole('DOCTOR'); setError(''); setSuccessMsg(''); }}
          >
            <Stethoscope size={14} />
            <span>Doctor</span>
          </button>

          <button
            className={`btn btn-sm ${role === 'ADMIN' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ border: 'none' }}
            onClick={() => { setRole('ADMIN'); setError(''); setSuccessMsg(''); }}
          >
            <Lock size={14} />
            <span>Admin</span>
          </button>
        </div>

        {/* Mode Toggle (Login / Sign Up) */}
        {role !== 'ADMIN' && (
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1.5rem', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
            <button
              type="button"
              style={{ background: 'none', border: 'none', color: mode === 'LOGIN' ? 'var(--accent-primary)' : 'var(--text-muted)', fontWeight: mode === 'LOGIN' ? 700 : 500, cursor: 'pointer', borderBottom: mode === 'LOGIN' ? '2px solid var(--accent-primary)' : '2px solid transparent', paddingBottom: '0.25rem' }}
              onClick={() => { setMode('LOGIN'); setError(''); setSuccessMsg(''); }}
            >
              Sign In
            </button>
            <button
              type="button"
              style={{ background: 'none', border: 'none', color: mode === 'SIGNUP' ? 'var(--accent-primary)' : 'var(--text-muted)', fontWeight: mode === 'SIGNUP' ? 700 : 500, cursor: 'pointer', borderBottom: mode === 'SIGNUP' ? '2px solid var(--accent-primary)' : '2px solid transparent', paddingBottom: '0.25rem' }}
              onClick={() => { setMode('SIGNUP'); setError(''); setSuccessMsg(''); }}
            >
              Create Account
            </button>
          </div>
        )}

        {/* Success Alert Banner */}
        {successMsg && (
          <div className="status-pill" style={{ background: 'rgba(16, 185, 129, 0.15)', borderColor: 'var(--accent-primary)', marginBottom: '1.25rem', justifyContent: 'center' }}>
            <CheckCircle2 size={16} />
            <span>{successMsg}</span>
          </div>
        )}

        {/* Error Alert Banner */}
        {error && (
          <div className="status-pill btn-danger" style={{ marginBottom: '1.25rem', justifyContent: 'center', textAlign: 'center' }}>
            <AlertCircle size={16} style={{ flexShrink: 0 }} />
            <span>{error}</span>
          </div>
        )}

        {/* Form Body */}
        <form onSubmit={handleSubmit}>
          {role === 'ADMIN' ? (
            <div style={{ textAlign: 'center', padding: '1rem 0' }}>
              <ShieldCheck size={40} style={{ color: 'var(--accent-primary)', marginBottom: '0.75rem' }} />
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '0.5rem' }}>Clinic Manager Portal</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
                Full administrative access to doctors, patients, billing & stream analytics.
              </p>
              <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                <span>Enter Admin Dashboard</span>
                <ArrowRight size={16} />
              </button>
            </div>
          ) : (
            <>
              <div className="form-group">
                <label>{role === 'DOCTOR' ? 'Doctor Name, ID, or Contact' : 'Patient Name, ID, or Contact'}</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder={role === 'DOCTOR' ? 'e.g. Dr. Ankit Kishore' : 'e.g. Suraj Kumar'}
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              {mode === 'SIGNUP' && (
                <>
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
                      <label>Contact (10 digits)</label>
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

                  {role === 'DOCTOR' ? (
                    <>
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
                          placeholder="e.g. MBBS, MD"
                          value={formData.qualification}
                          onChange={(e) => setFormData({ ...formData, qualification: e.target.value })}
                          required
                        />
                      </div>
                    </>
                  ) : (
                    <>
                      <div className="form-group">
                        <label>Medical History</label>
                        <input
                          type="text"
                          className="form-control"
                          placeholder="e.g. Hypertension, Seasonal Asthma"
                          value={formData.medicalHistory}
                          onChange={(e) => setFormData({ ...formData, medicalHistory: e.target.value })}
                        />
                      </div>

                      <div className="form-group">
                        <label>Known Allergies (Comma separated)</label>
                        <input
                          type="text"
                          className="form-control"
                          placeholder="e.g. Penicillin, Peanuts"
                          value={formData.allergies}
                          onChange={(e) => setFormData({ ...formData, allergies: e.target.value })}
                        />
                      </div>
                    </>
                  )}
                </>
              )}

              <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={loading}>
                <span>{loading ? 'Processing...' : mode === 'LOGIN' ? `Sign In as ${role}` : `Create ${role} Account`}</span>
                <ArrowRight size={16} />
              </button>
            </>
          )}
        </form>
      </div>
    </div>
  );
}
