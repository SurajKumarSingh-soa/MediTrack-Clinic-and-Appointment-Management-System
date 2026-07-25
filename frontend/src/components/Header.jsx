import React, { useState, useEffect } from 'react';
import { Save, Download, LogOut, CheckCircle2, AlertCircle, UserCheck, Sun, Moon } from 'lucide-react';
import { api } from '../services/api';

export default function Header({ activeTab, onRefresh, user, onLogout }) {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [theme, setTheme] = useState(() => localStorage.getItem('meditrack_theme') || 'dark');

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('meditrack_theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'dark' ? 'light' : 'dark'));
  };

  const titleMap = {
    dashboard: { title: 'Clinic Dashboard', desc: 'Real-time overview of doctors, patients, and clinic performance.' },
    doctors: { title: 'Doctor Management', desc: 'View, add, search, and manage specialized medical practitioners.' },
    patients: { title: 'Patient Directory', desc: 'Manage patient medical histories, contact details, and allergies.' },
    appointments: { title: 'Appointment Scheduler', desc: 'Book, manage, complete, and track patient appointments.' },
    billing: { title: 'Billing & Invoicing', desc: 'Generate bills with automatic 18% GST tax and emergency surcharges.' },
    ai: { title: 'AI Doctor Matcher', desc: 'Rule-based intelligent doctor recommendation based on symptoms.' },
    analytics: { title: 'Analytics & Streams', desc: 'Real-time Java 8 Stream statistics, revenue, and specialization metrics.' },
  };

  const current = titleMap[activeTab] || { title: 'MediTrack', desc: 'Clinic Management System' };

  const handleSaveData = async () => {
    setLoading(true);
    try {
      const res = await api.saveData();
      setMessage({ type: 'success', text: res.message || 'CSV Data saved successfully!' });
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to save CSV data: ' + err.message });
    } finally {
      setLoading(false);
      setTimeout(() => setMessage(null), 4000);
    }
  };

  const handleLoadData = async () => {
    setLoading(true);
    try {
      const res = await api.loadData();
      setMessage({ type: 'success', text: res.message || 'CSV Data loaded successfully!' });
      if (onRefresh) onRefresh();
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to load CSV data: ' + err.message });
    } finally {
      setLoading(false);
      setTimeout(() => setMessage(null), 4000);
    }
  };

  return (
    <header className="top-header">
      <div className="header-title-group">
        <h1>{current.title}</h1>
        <p>{current.desc}</p>
      </div>

      <div className="header-actions">
        {message && (
          <div className={`status-pill ${message.type === 'error' ? 'btn-danger' : ''}`} style={{ padding: '0.4rem 0.85rem' }}>
            {message.type === 'error' ? <AlertCircle size={14} /> : <CheckCircle2 size={14} />}
            <span>{message.text}</span>
          </div>
        )}

        {/* Theme Toggle Button */}
        <button 
          className="btn btn-secondary btn-sm" 
          onClick={toggleTheme}
          title="Toggle Light / Dark Theme"
          style={{ padding: '0.45rem' }}
        >
          {theme === 'dark' ? <Sun size={16} style={{ color: '#fbbf24' }} /> : <Moon size={16} style={{ color: '#8b5cf6' }} />}
        </button>

        {/* Logged in User Profile Pill */}
        {user && (
          <div className="status-pill" style={{ background: 'var(--bg-card)', border: '1px solid var(--border-color)', color: 'var(--text-primary)', padding: '0.4rem 0.85rem' }}>
            <UserCheck size={14} style={{ color: '#8b5cf6' }} />
            <span style={{ fontWeight: 600 }}>{user.name}</span>
            <span className="badge badge-spec" style={{ fontSize: '0.68rem', padding: '0.15rem 0.45rem' }}>{user.role}</span>
          </div>
        )}

        <button 
          className="btn btn-secondary btn-sm" 
          onClick={handleSaveData} 
          disabled={loading}
          title="Save state to data/*.csv files"
        >
          <Save size={15} />
          <span>Save CSV</span>
        </button>

        <button 
          className="btn btn-secondary btn-sm" 
          onClick={handleLoadData} 
          disabled={loading}
          title="Reload state from data/*.csv files"
        >
          <Download size={15} />
          <span>Load CSV</span>
        </button>

        <button
          className="btn btn-danger btn-sm"
          onClick={onLogout}
          title="Logout of current session"
        >
          <LogOut size={15} />
          <span>Logout</span>
        </button>
      </div>
    </header>
  );
}
