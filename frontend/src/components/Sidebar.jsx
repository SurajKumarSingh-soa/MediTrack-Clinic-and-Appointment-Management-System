import React from 'react';
import { 
  LayoutDashboard, 
  Stethoscope, 
  Users, 
  Calendar, 
  Receipt, 
  Sparkles, 
  BarChart3 
} from 'lucide-react';

export default function Sidebar({ activeTab, setActiveTab, role }) {
  const allNavItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, roles: ['ADMIN', 'DOCTOR', 'PATIENT'] },
    { id: 'doctors', label: 'Doctors Directory', icon: Stethoscope, roles: ['ADMIN', 'PATIENT'] }, // Removed DOCTOR
    { id: 'patients', label: 'Patients Directory', icon: Users, roles: ['ADMIN', 'DOCTOR'] },
    { id: 'appointments', label: 'Appointments', icon: Calendar, roles: ['ADMIN', 'DOCTOR', 'PATIENT'] },
    { id: 'billing', label: 'Billing & Invoicing', icon: Receipt, roles: ['ADMIN', 'DOCTOR', 'PATIENT'] },
    { id: 'ai', label: 'AI Doctor Matcher', icon: Sparkles, roles: ['ADMIN', 'PATIENT'] },
    { id: 'analytics', label: 'Analytics Dashboard', icon: BarChart3, roles: ['ADMIN', 'DOCTOR'] },
  ];

  const navItems = allNavItems.filter((item) => !role || item.roles.includes(role));

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">
          <Stethoscope size={22} />
        </div>
        <div className="sidebar-logo-text">MediTrack</div>
      </div>

      <nav style={{ flex: 1 }}>
        <ul className="nav-list">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <li key={item.id}>
                <button
                  className={`nav-item-btn ${isActive ? 'active' : ''}`}
                  onClick={() => setActiveTab(item.id)}
                >
                  <Icon size={18} />
                  <span>{item.label}</span>
                </button>
              </li>
            );
          })}
        </ul>
      </nav>

      <div style={{ marginTop: 'auto', paddingTop: '1.5rem', borderTop: '1px solid var(--border-color)' }}>
        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textAlign: 'center' }}>
          MediTrack • {role || 'Portal'} Mode
        </div>
      </div>
    </aside>
  );
}
