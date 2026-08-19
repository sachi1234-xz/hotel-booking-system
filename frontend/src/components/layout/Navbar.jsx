import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useState } from 'react';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
    setMenuOpen(false);
  };

  return (
    <nav className="bg-white shadow-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex items-center gap-2">
              <svg className="h-8 w-8 text-indigo-600" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 21v-8.25M15.75 21v-8.25M8.25 21v-8.25M3 9l9-6 9 6m-1.5 12V10.332A48.36 48.36 0 0012 9.75c-2.551 0-5.056.2-7.5.582V21M3 21h18M12 6.75h.008v.008H12V6.75z" />
              </svg>
              <span className="text-xl font-bold text-gray-900">StayEase</span>
            </Link>
            <div className="hidden md:ml-8 md:flex md:space-x-4">
              <Link to="/hotels" className="text-gray-600 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium transition-colors">Hotels</Link>
              {user && <Link to="/dashboard/bookings" className="text-gray-600 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium transition-colors">My Bookings</Link>}
              {user?.role === 'ADMIN' && <Link to="/admin" className="text-gray-600 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium transition-colors">Admin</Link>}
              <Link to="/about" className="text-gray-600 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium transition-colors">About</Link>
              <Link to="/contact" className="text-gray-600 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium transition-colors">Contact</Link>
            </div>
          </div>
          <div className="hidden md:flex md:items-center md:space-x-4">
            {user ? (
              <div className="flex items-center gap-4">
                <span className="text-sm text-gray-500">{user.email}</span>
                <span className="text-xs bg-indigo-100 text-indigo-700 px-2 py-1 rounded-full">{user.role}</span>
                <button onClick={handleLogout} className="bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-lg text-sm font-medium transition-colors">Logout</button>
              </div>
            ) : (
              <>
                <Link to="/login" className="text-gray-600 hover:text-indigo-600 px-4 py-2 rounded-lg text-sm font-medium transition-colors">Login</Link>
                <Link to="/register" className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">Sign Up</Link>
              </>
            )}
          </div>
          <button onClick={() => setMenuOpen(!menuOpen)} className="md:hidden flex items-center">
            <svg className="h-6 w-6 text-gray-600" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
              {menuOpen
                ? <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                : <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />}
            </svg>
          </button>
        </div>
      </div>
      {menuOpen && (
        <div className="md:hidden border-t bg-white px-4 pt-2 pb-4 space-y-1">
          <Link to="/hotels" onClick={() => setMenuOpen(false)} className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100">Hotels</Link>
          {user && <Link to="/dashboard/bookings" onClick={() => setMenuOpen(false)} className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100">My Bookings</Link>}
          {user?.role === 'ADMIN' && <Link to="/admin" onClick={() => setMenuOpen(false)} className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100">Admin</Link>}
          <Link to="/about" onClick={() => setMenuOpen(false)} className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100">About</Link>
          <Link to="/contact" onClick={() => setMenuOpen(false)} className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100">Contact</Link>
          <hr />
          {user ? (
            <>
              <span className="block px-3 py-2 text-sm text-gray-500">{user.email}</span>
              <button onClick={handleLogout} className="w-full text-left px-3 py-2 rounded-md text-base font-medium text-red-600 hover:bg-red-50">Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" onClick={() => setMenuOpen(false)} className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100">Login</Link>
              <Link to="/register" onClick={() => setMenuOpen(false)} className="block px-3 py-2 rounded-md text-base font-medium text-indigo-600 hover:bg-indigo-50">Sign Up</Link>
            </>
          )}
        </div>
      )}
    </nav>
  );
}
