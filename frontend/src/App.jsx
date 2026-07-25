import React, { useState } from 'react';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Dashboard from './components/Dashboard';
import DoctorManagement from './components/DoctorManagement';
import PatientManagement from './components/PatientManagement';
import AppointmentManagement from './components/AppointmentManagement';
import BillingManagement from './components/BillingManagement';
import AIRecommendation from './components/AIRecommendation';
import AnalyticsView from './components/AnalyticsView';
import AuthView from './components/AuthView';

export default function App() {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('meditrack_user');
    return saved ? JSON.parse(saved) : null;
  });

  const [activeTab, setActiveTab] = useState('dashboard');
  const [refreshKey, setRefreshKey] = useState(0);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem('meditrack_user', JSON.stringify(userData));
    setActiveTab('dashboard');
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('meditrack_user');
  };

  const handleRefresh = () => {
    setRefreshKey((prev) => prev + 1);
  };

  if (!user) {
    return <AuthView onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div className="app-container">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} role={user.role} />
      
      <div className="main-content">
        <Header 
          activeTab={activeTab} 
          onRefresh={handleRefresh} 
          user={user} 
          onLogout={handleLogout} 
        />

        <main className="page-wrapper" key={refreshKey}>
          {activeTab === 'dashboard' && <Dashboard setActiveTab={setActiveTab} user={user} />}
          {activeTab === 'doctors' && <DoctorManagement user={user} setActiveTab={setActiveTab} />}
          {activeTab === 'patients' && <PatientManagement user={user} />}
          {activeTab === 'appointments' && <AppointmentManagement user={user} />}
          {activeTab === 'billing' && <BillingManagement user={user} />}
          {activeTab === 'ai' && <AIRecommendation setActiveTab={setActiveTab} user={user} />}
          {activeTab === 'analytics' && <AnalyticsView user={user} />}
        </main>
      </div>
    </div>
  );
}
