import React, { useState, useEffect } from 'react';
import { Stethoscope, Users, Calendar, DollarSign, Sparkles, ArrowRight, CheckCircle2, XCircle } from 'lucide-react';
import { api } from '../services/api';

export default function Dashboard({ setActiveTab, user }) {
  const [analytics, setAnalytics] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, [user]);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const [analyticsRes, apptsRes, docsRes] = await Promise.all([
        api.getAnalytics().catch(() => null),
        api.getAppointments().catch(() => []),
        api.getDoctors().catch(() => []),
      ]);
      setAnalytics(analyticsRes);
      setAppointments(apptsRes);
      setDoctors(docsRes);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCompleteAppt = async (id) => {
    try {
      await api.completeAppointment(id);
      loadDashboardData();
    } catch (err) {
      alert('Error completing appointment: ' + err.message);
    }
  };

  const handleCancelAppt = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) return;
    try {
      await api.cancelAppointment(id);
      loadDashboardData();
    } catch (err) {
      alert('Error cancelling appointment: ' + err.message);
    }
  };

  if (loading) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading dashboard...</div>;
  }

  // --- DOCTOR DASHBOARD VIEW ---
  if (user?.role === 'DOCTOR') {
    const myAppts = appointments.filter(
      (a) => a.doctor?.id === user.id || a.doctor?.name?.toLowerCase().includes(user.name.toLowerCase())
    );
    const pendingAppts = myAppts.filter((a) => a.status === 'PENDING');
    const completedAppts = myAppts.filter((a) => a.status === 'COMPLETED');
    const totalEarnings = completedAppts.reduce((sum, a) => sum + (a.doctor?.consultationFee || 0), 0);

    return (
      <div className="animate-fade-in">
        {/* Welcome Banner */}
        <div className="glass-card" style={{ padding: '1.75rem', marginBottom: '1.75rem', background: 'linear-gradient(135deg, rgba(5, 150, 105, 0.12) 0%, rgba(13, 148, 136, 0.12) 100%)' }}>
          <h2 style={{ fontFamily: 'var(--font-editorial)', fontSize: '1.6rem', fontStyle: 'italic', marginBottom: '0.35rem' }}>
            Welcome, {user.name}!
          </h2>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>
            Doctor Portal • Specialization: <strong>{user.specialization || 'Specialist'}</strong> • Consultation Fee: <strong>₹{user.fee || 500}</strong>
          </p>
        </div>

        {/* Doctor Metrics */}
        <div className="metrics-grid">
          <div className="glass-card metric-card">
            <div className="metric-info">
              <p>My Appointments</p>
              <h3>{myAppts.length}</h3>
            </div>
            <div className="metric-icon" style={{ background: 'var(--accent-gradient)' }}>
              <Calendar size={22} />
            </div>
          </div>

          <div className="glass-card metric-card">
            <div className="metric-info">
              <p>Pending Consultations</p>
              <h3>{pendingAppts.length}</h3>
            </div>
            <div className="metric-icon" style={{ background: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)' }}>
              <Users size={22} />
            </div>
          </div>

          <div className="glass-card metric-card">
            <div className="metric-info">
              <p>Completed Visits</p>
              <h3>{completedAppts.length}</h3>
            </div>
            <div className="metric-icon" style={{ background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' }}>
              <CheckCircle2 size={22} />
            </div>
          </div>

          <div className="glass-card metric-card">
            <div className="metric-info">
              <p>Earned Fees</p>
              <h3>₹{totalEarnings.toFixed(0)}</h3>
            </div>
            <div className="metric-icon" style={{ background: 'linear-gradient(135deg, #06b6d4 0%, #0284c7 100%)' }}>
              <DollarSign size={22} />
            </div>
          </div>
        </div>

        {/* My Patient Schedule Table */}
        <div className="glass-card" style={{ padding: '1.5rem' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1.25rem' }}>My Assigned Patient Consultations</h3>
          {myAppts.length === 0 ? (
            <div style={{ padding: '2.5rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              No patient consultations assigned yet.
            </div>
          ) : (
            <div className="table-container">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Appt ID</th>
                    <th>Patient Name</th>
                    <th>Date & Time</th>
                    <th>Notes</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {myAppts.map((appt) => (
                    <tr key={appt.id}>
                      <td>#{appt.id}</td>
                      <td><strong>{appt.patient?.name}</strong> (Age: {appt.patient?.age})</td>
                      <td>{appt.appointmentDate}</td>
                      <td style={{ fontSize: '0.85rem' }}>{appt.notes}</td>
                      <td><span className={`badge badge-${appt.status.toLowerCase()}`}>{appt.status}</span></td>
                      <td>
                        {appt.status === 'PENDING' && (
                          <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <button
                              className="btn btn-sm"
                              style={{ background: 'rgba(16, 185, 129, 0.15)', color: '#10b981', padding: '0.3rem 0.6rem' }}
                              onClick={() => handleCompleteAppt(appt.id)}
                            >
                              <CheckCircle2 size={14} />
                              <span>Complete</span>
                            </button>
                            <button
                              className="btn btn-danger btn-sm"
                              style={{ padding: '0.3rem 0.6rem' }}
                              onClick={() => handleCancelAppt(appt.id)}
                            >
                              <XCircle size={14} />
                            </button>
                          </div>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    );
  }

  // --- PATIENT DASHBOARD VIEW ---
  if (user?.role === 'PATIENT') {
    const myAppts = appointments.filter(
      (a) => a.patient?.id === user.id || a.patient?.name?.toLowerCase().includes(user.name.toLowerCase())
    );

    return (
      <div className="animate-fade-in">
        {/* Welcome Banner */}
        <div className="glass-card" style={{ padding: '1.75rem', marginBottom: '1.75rem', background: 'linear-gradient(135deg, rgba(13, 148, 136, 0.12) 0%, rgba(5, 150, 105, 0.12) 100%)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2 style={{ fontFamily: 'var(--font-editorial)', fontSize: '1.6rem', fontStyle: 'italic', marginBottom: '0.35rem' }}>
                Welcome, {user.name}!
              </h2>
              <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>
                Patient Medical Portal • View your bookings & match with doctor specialists.
              </p>
            </div>
            <button className="btn btn-primary" onClick={() => setActiveTab('appointments')}>
              <Calendar size={16} />
              <span>Book Appointment</span>
            </button>
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1.5rem' }}>
          {/* Patient's Appointments */}
          <div className="glass-card" style={{ padding: '1.5rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1.25rem' }}>My Booked Consultations</h3>
            {myAppts.length === 0 ? (
              <div style={{ padding: '2.5rem', textAlign: 'center', color: 'var(--text-muted)' }}>
                You have no scheduled appointments yet. Click "Book Appointment" to schedule your doctor visit!
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Appt ID</th>
                      <th>Attending Doctor</th>
                      <th>Specialization</th>
                      <th>Date & Time</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {myAppts.map((appt) => (
                      <tr key={appt.id}>
                        <td>#{appt.id}</td>
                        <td><strong>{appt.doctor?.name}</strong></td>
                        <td><span className="badge badge-spec">{appt.doctor?.specialization}</span></td>
                        <td>{appt.appointmentDate}</td>
                        <td><span className={`badge badge-${appt.status.toLowerCase()}`}>{appt.status}</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Quick AI Matcher Callout */}
          <div className="glass-card" style={{ padding: '1.5rem', background: 'var(--bg-subtle)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', marginBottom: '1rem', color: 'var(--accent-primary)' }}>
              <Sparkles size={22} />
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Need a Doctor Recommendation?</h3>
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
              Describe your symptoms and our AI Matcher will match you with the right specialist instantly.
            </p>
            <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => setActiveTab('ai')}>
              <Sparkles size={16} />
              <span>Launch AI Symptom Matcher</span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  // --- ADMIN / MANAGER DASHBOARD VIEW ---
  return (
    <div className="animate-fade-in">
      {/* Metric Cards Grid */}
      <div className="metrics-grid">
        <div className="glass-card metric-card">
          <div className="metric-info">
            <p>Total Doctors</p>
            <h3>{analytics?.totalDoctors || doctors.length || 0}</h3>
          </div>
          <div className="metric-icon" style={{ background: 'linear-gradient(135deg, #06b6d4 0%, #0284c7 100%)' }}>
            <Stethoscope size={24} />
          </div>
        </div>

        <div className="glass-card metric-card">
          <div className="metric-info">
            <p>Total Patients</p>
            <h3>{analytics?.totalPatients || 0}</h3>
          </div>
          <div className="metric-icon" style={{ background: 'var(--accent-gradient)' }}>
            <Users size={24} />
          </div>
        </div>

        <div className="glass-card metric-card">
          <div className="metric-info">
            <p>Total Appointments</p>
            <h3>{analytics?.totalAppointments || appointments.length || 0}</h3>
          </div>
          <div className="metric-icon" style={{ background: 'linear-gradient(135deg, #8b5cf6 0%, #ec4899 100%)' }}>
            <Calendar size={24} />
          </div>
        </div>

        <div className="glass-card metric-card">
          <div className="metric-info">
            <p>Avg Doctor Fee</p>
            <h3>₹{analytics?.averageDoctorFee ? analytics.averageDoctorFee.toFixed(0) : '0'}</h3>
          </div>
          <div className="metric-icon" style={{ background: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)' }}>
            <DollarSign size={24} />
          </div>
        </div>
      </div>

      {/* Grid Section: Recent Appointments & AI Matcher CTA */}
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
        <div className="glass-card" style={{ padding: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
            <h2 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Recent Clinic Activity</h2>
            <button className="btn btn-secondary btn-sm" onClick={() => setActiveTab('appointments')}>
              <span>View All</span>
              <ArrowRight size={14} />
            </button>
          </div>

          {appointments.length === 0 ? (
            <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              No appointments booked yet.
            </div>
          ) : (
            <div className="table-container">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Patient</th>
                    <th>Doctor</th>
                    <th>Date & Time</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.slice(0, 5).map((appt) => (
                    <tr key={appt.id}>
                      <td>#{appt.id}</td>
                      <td>{appt.patient?.name || 'Unknown'}</td>
                      <td>{appt.doctor?.name || 'Unknown'}</td>
                      <td>{appt.appointmentDate}</td>
                      <td>
                        <span className={`badge badge-${appt.status.toLowerCase()}`}>
                          {appt.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        <div className="glass-card" style={{ padding: '1.5rem', background: 'var(--bg-subtle)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', marginBottom: '1rem', color: 'var(--accent-primary)' }}>
            <Sparkles size={20} />
            <h2 style={{ fontSize: '1.1rem', fontWeight: 600 }}>AI Doctor Recommendation</h2>
          </div>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '1.25rem' }}>
            Rule-based symptom engine matching patients with specialized doctors.
          </p>

          <button 
            className="btn btn-primary" 
            style={{ width: '100%' }}
            onClick={() => setActiveTab('ai')}
          >
            <Sparkles size={16} />
            <span>Launch AI Symptom Matcher</span>
          </button>
        </div>
      </div>
    </div>
  );
}
