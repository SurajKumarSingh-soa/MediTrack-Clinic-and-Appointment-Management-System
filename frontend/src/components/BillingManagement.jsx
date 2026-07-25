import React, { useState, useEffect } from 'react';
import { Receipt, Printer, CheckCircle2 } from 'lucide-react';
import { api } from '../services/api';

export default function BillingManagement({ user }) {
  const [appointments, setAppointments] = useState([]);
  const [selectedApptId, setSelectedApptId] = useState('');
  const [additionalCharges, setAdditionalCharges] = useState('0');
  const [isEmergency, setIsEmergency] = useState(false);
  const [generatedBill, setGeneratedBill] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const isPatient = user?.role === 'PATIENT';

  useEffect(() => {
    loadAppointments();
  }, [user]);

  const loadAppointments = async () => {
    try {
      const appts = await api.getAppointments();
      setAppointments(appts);
      if (appts.length > 0) setSelectedApptId(appts[0].id);
    } catch (err) {
      console.error(err);
    }
  };

  const handleGenerateBill = async (e) => {
    e.preventDefault();
    setError('');
    if (!selectedApptId) return setError('Please select an appointment');

    setLoading(true);
    try {
      const bill = await api.generateBill({
        appointmentId: parseInt(selectedApptId),
        additionalCharges: parseFloat(additionalCharges) || 0,
        isEmergency,
      });
      setGeneratedBill(bill);
    } catch (err) {
      setError(err.message || 'Failed to generate bill');
    } finally {
      setLoading(false);
    }
  };

  // --- PATIENT BILLING VIEW: View current & previous invoices ---
  if (isPatient) {
    const myAppts = appointments.filter(
      (a) => a.patient?.id === user.id || a.patient?.name?.toLowerCase().includes(user.name.toLowerCase())
    );

    return (
      <div className="animate-fade-in">
        <div className="glass-card" style={{ padding: '1.75rem', marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', marginBottom: '0.5rem' }}>
            <Receipt size={22} style={{ color: 'var(--accent-primary)' }} />
            <h2 style={{ fontSize: '1.2rem', fontWeight: 700 }}>My Medical Invoices & Bills</h2>
          </div>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            View medical consultation receipts, breakdown, and GST calculations for your visits.
          </p>
        </div>

        {myAppts.length === 0 ? (
          <div className="glass-card" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            No consultation bills available.
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(360px, 1fr))', gap: '1.5rem' }}>
            {myAppts.map((appt) => {
              const fee = appt.doctor?.consultationFee || 500;
              const gst = fee * 0.18;
              const total = fee + gst;

              return (
                <div key={appt.id} className="glass-card" style={{ padding: '1.5rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.75rem' }}>
                    <div>
                      <span className="badge badge-spec" style={{ fontSize: '0.7rem' }}>Invoice #{appt.id}</span>
                      <h3 style={{ fontSize: '1.05rem', fontWeight: 700, marginTop: '0.2rem' }}>Dr. {appt.doctor?.name}</h3>
                    </div>
                    <button className="btn btn-secondary btn-sm" onClick={() => window.print()}>
                      <Printer size={14} />
                      <span>Print</span>
                    </button>
                  </div>

                  <div style={{ fontSize: '0.85rem', display: 'flex', flexDirection: 'column', gap: '0.4rem', color: 'var(--text-secondary)', marginBottom: '1rem' }}>
                    <div><strong>Specialization:</strong> {appt.doctor?.specialization}</div>
                    <div><strong>Date:</strong> {appt.appointmentDate}</div>
                    <div><strong>Status:</strong> <span className={`badge badge-${appt.status.toLowerCase()}`}>{appt.status}</span></div>
                  </div>

                  <div style={{ background: 'var(--bg-subtle)', padding: '0.85rem', borderRadius: '10px', fontSize: '0.85rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.35rem' }}>
                      <span>Consultation Fee:</span>
                      <span>₹{fee.toFixed(2)}</span>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.35rem', color: 'var(--text-muted)' }}>
                      <span>GST (18%):</span>
                      <span>+₹{gst.toFixed(2)}</span>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', paddingTop: '0.5rem', borderTop: '1px dashed var(--border-color)', fontWeight: 700, fontSize: '1rem' }}>
                      <span>Total Paid:</span>
                      <span style={{ color: 'var(--accent-primary)' }}>₹{total.toFixed(2)}</span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    );
  }

  // --- DOCTOR & ADMIN BILLING VIEW: Generate Bills for Patients ---
  return (
    <div className="animate-fade-in">
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
        {/* Form Container */}
        <div className="glass-card" style={{ padding: '1.75rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', marginBottom: '1.25rem' }}>
            <Receipt size={22} style={{ color: 'var(--accent-primary)' }} />
            <h2 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Generate Patient Invoice</h2>
          </div>

          {error && <div className="status-pill btn-danger" style={{ marginBottom: '1rem' }}>{error}</div>}

          <form onSubmit={handleGenerateBill}>
            <div className="form-group">
              <label>Select Patient Appointment</label>
              <select
                className="form-control"
                value={selectedApptId}
                onChange={(e) => setSelectedApptId(e.target.value)}
                required
              >
                {appointments.length === 0 ? (
                  <option value="">No appointments available</option>
                ) : (
                  appointments.map((a) => (
                    <option key={a.id} value={a.id}>
                      Appt #{a.id} — Patient: {a.patient?.name} | Doctor: {a.doctor?.name} (Fee: ₹{a.doctor?.consultationFee})
                    </option>
                  ))
                )}
              </select>
            </div>

            <div className="form-group">
              <label>Additional Lab / Medication Charges (₹)</label>
              <input
                type="number"
                className="form-control"
                value={additionalCharges}
                onChange={(e) => setAdditionalCharges(e.target.value)}
                placeholder="0.00"
              />
            </div>

            <div className="form-group" style={{ flexDirection: 'row', alignItems: 'center', gap: '0.75rem', marginTop: '0.5rem' }}>
              <input
                type="checkbox"
                id="emergency"
                checked={isEmergency}
                onChange={(e) => setIsEmergency(e.target.checked)}
                style={{ width: '18px', height: '18px', accentColor: 'var(--accent-primary)' }}
              />
              <label htmlFor="emergency" style={{ cursor: 'pointer', userSelect: 'none', margin: 0 }}>
                Emergency Visit (Adds ₹500.00 Surcharge)
              </label>
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1.5rem' }} disabled={loading}>
              <Receipt size={16} />
              <span>{loading ? 'Calculating...' : 'Generate Bill for Patient'}</span>
            </button>
          </form>
        </div>

        {/* Invoice Preview */}
        <div>
          {generatedBill ? (
            <div className="glass-card" style={{ padding: '2rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem' }}>
                <div>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--accent-primary)' }}>MediTrack Invoice</h3>
                  <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Bill ID: #{generatedBill.billId}</p>
                </div>
                <button className="btn btn-secondary btn-sm" onClick={() => window.print()}>
                  <Printer size={14} />
                  <span>Print Receipt</span>
                </button>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Patient Name:</span>
                  <strong>{generatedBill.patientName}</strong>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Attending Doctor:</span>
                  <strong>{generatedBill.doctorName}</strong>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Date Generated:</span>
                  <span>{generatedBill.generatedDate}</span>
                </div>
              </div>

              <div style={{ background: 'var(--bg-subtle)', borderRadius: 'var(--radius-sm)', padding: '1rem', marginBottom: '1.5rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.85rem' }}>
                  <span>Doctor Consultation Fee:</span>
                  <span>₹{generatedBill.consultationFee.toFixed(2)}</span>
                </div>
                {generatedBill.additionalCharges > 0 && (
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.85rem' }}>
                    <span>Additional Lab Charges:</span>
                    <span>₹{generatedBill.additionalCharges.toFixed(2)}</span>
                  </div>
                )}
                {generatedBill.isEmergency && (
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.85rem', color: '#f59e0b' }}>
                    <span>Emergency Surcharge:</span>
                    <span>+₹500.00</span>
                  </div>
                )}
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.85rem' }}>
                  <span>Subtotal:</span>
                  <span>₹{generatedBill.totalAmount.toFixed(2)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                  <span>GST (18%):</span>
                  <span>+₹{generatedBill.tax.toFixed(2)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '0.75rem', paddingTop: '0.75rem', borderTop: '1px dashed var(--border-color)', fontSize: '1.2rem', fontWeight: 800 }}>
                  <span>Final Total:</span>
                  <span style={{ color: 'var(--accent-primary)' }}>₹{generatedBill.finalAmount.toFixed(2)}</span>
                </div>
              </div>
            </div>
          ) : (
            <div className="glass-card" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              Select an appointment and click "Generate Bill for Patient" to create an official invoice.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
