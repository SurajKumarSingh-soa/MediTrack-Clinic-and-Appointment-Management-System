import React, { useState, useEffect } from 'react';
import { Calendar, Plus, CheckCircle2, XCircle, X } from 'lucide-react';
import { api } from '../services/api';

export default function AppointmentManagement({ user }) {
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [patients, setPatients] = useState([]);
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    patientId: '',
    doctorId: '',
    date: new Date().toISOString().slice(0, 10) + ' 10:00',
    notes: 'Routine Consultation',
  });

  useEffect(() => {
    loadData();
  }, [user]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [appts, docs, pats] = await Promise.all([
        api.getAppointments(),
        api.getDoctors(),
        api.getPatients(),
      ]);
      setAppointments(appts);
      setDoctors(docs);
      setPatients(pats);

      // Auto-set Patient ID if user is logged in as Patient
      let selectedPatientId = pats.length > 0 ? pats[0].id : '';
      if (user?.role === 'PATIENT') {
        const found = pats.find((p) => p.id === user.id || p.name.toLowerCase() === user.name.toLowerCase());
        if (found) selectedPatientId = found.id;
      }

      setFormData((prev) => ({
        ...prev,
        patientId: selectedPatientId,
        doctorId: docs.length > 0 ? docs[0].id : '',
      }));
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleBookAppointment = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.patientId || !formData.doctorId) {
      return setError('Please select both a patient and a doctor');
    }

    try {
      await api.bookAppointment({
        patientId: parseInt(formData.patientId),
        doctorId: parseInt(formData.doctorId),
        date: formData.date,
        notes: formData.notes,
      });
      setShowModal(false);
      loadData();
    } catch (err) {
      setError(err.message || 'Failed to book appointment');
    }
  };

  const handleComplete = async (id) => {
    try {
      await api.completeAppointment(id);
      loadData();
    } catch (err) {
      alert('Error completing appointment: ' + err.message);
    }
  };

  const handleCancel = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) return;
    try {
      await api.cancelAppointment(id);
      loadData();
    } catch (err) {
      alert('Error cancelling appointment: ' + err.message);
    }
  };

  // Filter list based on role
  let roleFiltered = appointments;
  if (user?.role === 'PATIENT') {
    roleFiltered = appointments.filter((a) => a.patient?.id === user.id || a.patient?.name?.toLowerCase().includes(user.name.toLowerCase()));
  } else if (user?.role === 'DOCTOR') {
    roleFiltered = appointments.filter((a) => a.doctor?.id === user.id || a.doctor?.name?.toLowerCase().includes(user.name.toLowerCase()));
  }

  const finalAppointments = roleFiltered.filter((appt) => {
    if (filterStatus === 'ALL') return true;
    return appt.status === filterStatus;
  });

  const canManageStatus = user?.role === 'ADMIN' || user?.role === 'DOCTOR';

  return (
    <div className="animate-fade-in">
      {/* Controls */}
      <div style={{ display: 'flex', gap: '1rem', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          {['ALL', 'PENDING', 'COMPLETED', 'CANCELLED'].map((status) => (
            <button
              key={status}
              className={`btn btn-sm ${filterStatus === status ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setFilterStatus(status)}
            >
              {status}
            </button>
          ))}
        </div>

        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          <Plus size={16} />
          <span>Book Appointment</span>
        </button>
      </div>

      {/* Table Container */}
      <div className="glass-card" style={{ padding: '1.5rem' }}>
        {loading ? (
          <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading appointments...</div>
        ) : finalAppointments.length === 0 ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            No appointments found for status "{filterStatus}".
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Appt ID</th>
                  <th>Patient</th>
                  <th>Doctor</th>
                  <th>Specialization</th>
                  <th>Scheduled Date</th>
                  <th>Notes</th>
                  <th>Status</th>
                  {canManageStatus && <th>Actions</th>}
                </tr>
              </thead>
              <tbody>
                {finalAppointments.map((appt) => (
                  <tr key={appt.id}>
                    <td>#{appt.id}</td>
                    <td>
                      <strong>{appt.patient?.name}</strong>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>ID: #{appt.patient?.id}</div>
                    </td>
                    <td>
                      <strong>{appt.doctor?.name}</strong>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Fee: ₹{appt.doctor?.consultationFee}</div>
                    </td>
                    <td>
                      <span className="badge badge-spec">{appt.doctor?.specialization}</span>
                    </td>
                    <td>{appt.appointmentDate}</td>
                    <td style={{ fontSize: '0.85rem' }}>{appt.notes}</td>
                    <td>
                      <span className={`badge badge-${appt.status.toLowerCase()}`}>
                        {appt.status}
                      </span>
                    </td>
                    {canManageStatus && (
                      <td>
                        {appt.status === 'PENDING' && (
                          <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <button
                              className="btn btn-sm"
                              style={{ background: 'rgba(16, 185, 129, 0.15)', color: '#34d399', padding: '0.35rem 0.6rem' }}
                              onClick={() => handleComplete(appt.id)}
                              title="Mark as Completed"
                            >
                              <CheckCircle2 size={14} />
                            </button>
                            <button
                              className="btn btn-danger btn-sm"
                              style={{ padding: '0.35rem 0.6rem' }}
                              onClick={() => handleCancel(appt.id)}
                              title="Cancel Appointment"
                            >
                              <XCircle size={14} />
                            </button>
                          </div>
                        )}
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Schedule Appointment Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Schedule New Appointment</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}><X size={20} /></button>
            </div>

            {error && <div className="status-pill btn-danger" style={{ marginBottom: '1rem' }}>{error}</div>}

            <form onSubmit={handleBookAppointment}>
              <div className="form-group">
                <label>Select Patient</label>
                {user?.role === 'PATIENT' ? (
                  <input
                    type="text"
                    className="form-control"
                    value={`${user.name} (My Profile)`}
                    disabled
                  />
                ) : (
                  <select
                    className="form-control"
                    value={formData.patientId}
                    onChange={(e) => setFormData({ ...formData, patientId: e.target.value })}
                    required
                  >
                    {patients.map((p) => (
                      <option key={p.id} value={p.id}>{p.name} (ID: #{p.id}, Age: {p.age})</option>
                    ))}
                  </select>
                )}
              </div>

              <div className="form-group">
                <label>Select Doctor</label>
                <select
                  className="form-control"
                  value={formData.doctorId}
                  onChange={(e) => setFormData({ ...formData, doctorId: e.target.value })}
                  required
                >
                  {doctors.map((d) => (
                    <option key={d.id} value={d.id}>{d.name} ({d.specialization} • Fee: ₹{d.consultationFee})</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Date & Time (YYYY-MM-DD HH:mm)</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. 2026-07-26 14:30"
                  value={formData.date}
                  onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Consultation Notes / Reason</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Regular follow up checkup"
                  value={formData.notes}
                  onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Confirm Booking</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
