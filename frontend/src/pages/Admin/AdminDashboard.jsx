import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import ManageHotels from './ManageHotels';
import ManageRooms from './ManageRooms';

const TABS = [
  { key: 'hotels', label: 'Hotels', icon: '🏨' },
  { key: 'rooms', label: 'Rooms', icon: '🛏️' },
];

export default function AdminDashboard() {
  const [tab, setTab] = useState('hotels');

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-8 gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="text-gray-500 mt-1">Manage hotels and rooms</p>
        </div>
        <div className="flex gap-2">
          {TABS.map((t) => (
            <button key={t.key} onClick={() => setTab(t.key)} className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${tab === t.key ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}`}>
              {t.icon} {t.label}
            </button>
          ))}
        </div>
      </div>

      {tab === 'hotels' && <ManageHotels />}
      {tab === 'rooms' && <ManageRooms />}
    </div>
  );
}
