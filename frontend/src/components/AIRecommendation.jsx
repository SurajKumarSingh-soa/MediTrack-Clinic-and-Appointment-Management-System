import React, { useState } from 'react';
import { Sparkles, ArrowRight, Stethoscope, CheckCircle2, Award, Calendar } from 'lucide-react';
import { api } from '../services/api';

export default function AIRecommendation({ setActiveTab }) {
  const [symptoms, setSymptoms] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const sampleQueries = [
    'chest pain and palpitation',
    'severe headache and dizziness',
    'skin rash and itching',
    'fever and throat pain',
    'joint pain and swelling',
  ];

  const handleMatch = async (queryText) => {
    const textToSearch = queryText || symptoms;
    if (!textToSearch.trim()) return setError('Please enter or select patient symptoms');

    setError('');
    setLoading(true);
    try {
      const res = await api.getAIRecommendation(textToSearch);
      setResult(res);
    } catch (err) {
      setError(err.message || 'Failed to get recommendation');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '900px', margin: '0 auto' }}>
      {/* Input Card */}
      <div className="glass-card" style={{ padding: '2rem', marginBottom: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
          <div style={{ width: '38px', height: '38px', background: 'var(--accent-gradient)', borderRadius: 'var(--radius-sm)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white' }}>
            <Sparkles size={20} />
          </div>
          <div>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 800 }}>AI Doctor Recommendation Engine</h2>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Analyze patient symptoms to match the optimal specialization with confidence scoring.</p>
          </div>
        </div>

        {error && <div className="status-pill btn-danger" style={{ marginBottom: '1rem' }}>{error}</div>}

        <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
          <input
            type="text"
            className="form-control"
            style={{ flex: 1, padding: '0.85rem 1.15rem', fontSize: '1rem' }}
            placeholder="Type symptoms (e.g., chest pain, skin rash, severe headache)..."
            value={symptoms}
            onChange={(e) => setSymptoms(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleMatch()}
          />
          <button className="btn btn-primary" onClick={() => handleMatch()} disabled={loading}>
            <Sparkles size={16} />
            <span>{loading ? 'Analyzing...' : 'Find Doctor'}</span>
          </button>
        </div>

        {/* Quick Sample Query Chips */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Try example:</span>
          {sampleQueries.map((sample, idx) => (
            <button
              key={idx}
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '0.75rem', borderRadius: 'var(--radius-full)' }}
              onClick={() => {
                setSymptoms(sample);
                handleMatch(sample);
              }}
            >
              "{sample}"
            </button>
          ))}
        </div>
      </div>

      {/* Result Display */}
      {result && (
        <div className="glass-card animate-fade-in" style={{ padding: '2rem', border: '1px solid var(--border-glow)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Recommended Specialization</span>
              <h3 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--accent-primary)', marginTop: '0.2rem' }}>
                {result.recommendedSpecialization}
              </h3>
            </div>

            {/* Confidence Score Bar */}
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Match Confidence</div>
              <div style={{ fontSize: '1.35rem', fontWeight: 800, color: 'var(--accent-secondary)' }}>
                {(result.confidenceScore * 100).toFixed(0)}%
              </div>
              <div style={{ width: '120px', height: '6px', background: 'var(--bg-primary)', borderRadius: '3px', overflow: 'hidden', marginTop: '0.35rem' }}>
                <div style={{ width: `${result.confidenceScore * 100}%`, height: '100%', background: 'var(--accent-gradient)' }}></div>
              </div>
            </div>
          </div>

          <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '1rem' }}>Matching Specialists:</h4>

          {result.recommendedDoctors && result.recommendedDoctors.length > 0 ? (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '1rem' }}>
              {result.recommendedDoctors.map((doc) => (
                <div key={doc.id} style={{ background: 'var(--bg-primary)', padding: '1.25rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)' }}>
                  <h4 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '0.25rem' }}>{doc.name}</h4>
                  <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>{doc.qualification}</p>

                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '0.5rem', paddingTop: '0.5rem', borderTop: '1px solid var(--border-color)' }}>
                    <span style={{ fontSize: '0.9rem', fontWeight: 700, color: 'var(--accent-primary)' }}>₹{doc.consultationFee.toFixed(2)}</span>
                    <button
                      className="btn btn-primary btn-sm"
                      onClick={() => setActiveTab('appointments')}
                    >
                      <Calendar size={13} />
                      <span>Book</span>
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              No doctors currently registered under {result.recommendedSpecialization}. You can add one in Doctor Management!
            </p>
          )}
        </div>
      )}
    </div>
  );
}
