import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { bookingAPI } from '../../services/apiEndpoints';
import { useAuth } from '../../context/AuthContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';

const STATUS_STYLES = {
  CONFIRMED: 'bg-green-100 text-green-700',
  PENDING: 'bg-yellow-100 text-yellow-700',
  CANCELLED: 'bg-red-100 text-red-700',
  COMPLETED: 'bg-blue-100 text-blue-700',
};

export default function MyBookings() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchBookings = () => {
    if (!user?.email) return;
    setLoading(true);
    bookingAPI.getByUserId(user.email)
      .then((res) => setBookings(res.data))
      .catch(() => setError('Failed to load bookings'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchBookings(); }, [user]);

  const handleCancel = async (id) => {
    if (!confirm('Are you sure you want to cancel this booking?')) return;
    try {
      await bookingAPI.cancel(id);
      fetchBookings();
    } catch {
      setError('Failed to cancel booking');
    }
  };

  if (loading) return <div className="py-20"><LoadingSpinner size="lg" /></div>;

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 py-12">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">My Bookings</h1>
      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      {bookings.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl shadow">
          <p className="text-gray-500 text-lg mb-4">You haven't made any bookings yet.</p>
          <Link to="/hotels" className="bg-indigo-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors">Browse Hotels</Link>
        </div>
      ) : (
        <div className="space-y-4">
          {bookings.map((b) => (
            <div key={b.id} className="bg-white rounded-xl shadow-md border border-gray-100 p-6">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <h3 className="font-bold text-gray-900">Booking #{b.id}</h3>
                    <span className={`text-xs font-medium px-2.5 py-0.5 rounded-full ${STATUS_STYLES[b.status] || 'bg-gray-100 text-gray-700'}`}>{b.status}</span>
                  </div>
                  <div className="text-sm text-gray-600 space-y-1">
                    <p>Hotel #{b.hotelId} &middot; Room #{b.roomId}</p>
                    <p>Check-in: {b.checkInDate} &rarr; Check-out: {b.checkOutDate}</p>
                    {b.numberOfGuests && <p>Guests: {b.numberOfGuests}</p>}
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-xl font-bold text-indigo-600">${b.totalAmount?.toFixed(2) || b.totalAmount}</p>
                  {(b.status === 'CONFIRMED' || b.status === 'PENDING') && (
                    <button onClick={() => handleCancel(b.id)} className="mt-2 text-sm text-red-600 hover:underline">Cancel Booking</button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
