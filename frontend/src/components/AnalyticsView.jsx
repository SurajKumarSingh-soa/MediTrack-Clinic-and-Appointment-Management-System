import React, { useState, useEffect } from 'react';
import { BarChart3, RefreshCw, Activity, CheckCircle2, Clock, XCircle, TrendingUp } from 'lucide-react';
import { api } from '../services/api';

export default function AnalyticsView() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAnalytics();
  }, []);

  const loadAnalytics = async () => {
    setLoading(true);
    try {
      const data = await api.getAnalytics();
      setAnalytics(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading Stream Analytics...</div>;
  }

  if (!analytics) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>No analytics data available.</div>;
  }

  const specBreakdown = analytics.specializationBreakdown || {};
  const totalDocs = analytics.totalDoctors || 1;
  const totalAppts = analytics.totalAppointments || 1;

  const getPercentage = (val) => ((val / totalAppts) * 100).toFixed(0);

  return (
    <div className="animate-fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <BarChart3 size={24} style={{ color: 'var(--accent-emerald)' }} />
          <div>
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '1.35rem', fontWeight: 700 }}>Java 8 Streams Real-Time Analytics</h2>
            <p style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>Powered by backend stream aggregations & mapping</p>
          </div>
        </div>
        <button className="btn btn-secondary btn-sm" onClick={loadAnalytics}>
          <RefreshCw size={14} />
          <span>Refresh Data</span>
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
        {/* Appointment Status Breakdown */}
        <div className="glass-card" style={{ padding: '1.75rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
            <Activity size={18} style={{ color: 'var(--accent-cyan)' }} />
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Appointment Status Breakdown</h3>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem', marginBottom: '0.4rem' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-secondary)' }}>
                  <CheckCircle2 size={14} style={{ color: '#10b981' }} />
                  Completed Consultations
                </span>
                <strong>{analytics.completedAppointments} ({getPercentage(analytics.completedAppointments)}%)</strong>
              </div>
              <div style={{ width: '100%', height: '10px', background: 'var(--bg-primary)', borderRadius: '5px', overflow: 'hidden' }}>
                <div style={{ width: `${getPercentage(analytics.completedAppointments)}%`, height: '100%', background: 'linear-gradient(90deg, #10b981 0%, #34d399 100%)', transition: 'width 0.8s ease' }}></div>
              </div>
            </div>

            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem', marginBottom: '0.4rem' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-secondary)' }}>
                  <Clock size={14} style={{ color: '#f59e0b' }} />
                  Pending Visits
                </span>
                <strong>{analytics.pendingAppointments} ({getPercentage(analytics.pendingAppointments)}%)</strong>
              </div>
              <div style={{ width: '100%', height: '10px', background: 'var(--bg-primary)', borderRadius: '5px', overflow: 'hidden' }}>
                <div style={{ width: `${getPercentage(analytics.pendingAppointments)}%`, height: '100%', background: 'linear-gradient(90deg, #f59e0b 0%, #fbbf24 100%)', transition: 'width 0.8s ease' }}></div>
              </div>
            </div>

            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem', marginBottom: '0.4rem' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-secondary)' }}>
                  <XCircle size={14} style={{ color: '#f43f5e' }} />
                  Cancelled Visits
                </span>
                <strong>{analytics.cancelledAppointments} ({getPercentage(analytics.cancelledAppointments)}%)</strong>
              </div>
              <div style={{ width: '100%', height: '10px', background: 'var(--bg-primary)', borderRadius: '5px', overflow: 'hidden' }}>
                <div style={{ width: `${getPercentage(analytics.cancelledAppointments)}%`, height: '100%', background: 'linear-gradient(90deg, #f43f5e 0%, #fb7185 100%)', transition: 'width 0.8s ease' }}></div>
              </div>
            </div>
          </div>
        </div>

        {/* Doctor Specialization Distribution */}
        <div className="glass-card" style={{ padding: '1.75rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
            <TrendingUp size={18} style={{ color: 'var(--accent-violet)' }} />
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Specialization Distribution</h3>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {Object.keys(specBreakdown).length === 0 ? (
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No doctors added yet.</p>
            ) : (
              Object.entries(specBreakdown).map(([spec, count]) => (
                <div key={spec}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem', marginBottom: '0.35rem' }}>
                    <span className="badge badge-spec">{spec}</span>
                    <span><strong>{count} Doctors</strong> ({((count / totalDocs) * 100).toFixed(0)}%)</span>
                  </div>
                  <div style={{ width: '100%', height: '8px', background: 'var(--bg-primary)', borderRadius: '4px', overflow: 'hidden' }}>
                    <div style={{ width: `${(count / totalDocs) * 100}%`, height: '100%', background: 'var(--gradient-cyan)', transition: 'width 0.8s ease' }}></div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
