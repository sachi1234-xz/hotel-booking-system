import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { bookingAPI, hotelAPI } from '../services/apiEndpoints';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { Alert } from '../components/common/Alert';

export default function BookingPage() {
  const [searchParams] = useSearchParams();
  const hotelId = searchParams.get('hotelId');
  const roomId = searchParams.get('roomId');
  const navigate = useNavigate();
  const { user } = useAuth();

  const [hotel, setHotel] = useState(null);
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const [checkInDate, setCheckInDate] = useState('');
  const [checkOutDate, setCheckOutDate] = useState('');
  const [numberOfGuests, setNumberOfGuests] = useState(1);

  useEffect(() => {
    if (!hotelId) { setLoading(false); return; }
    hotelAPI.getById(hotelId)
      .then((res) => {
        setHotel(res.data);
        const found = (res.data.rooms || []).find((r) => String(r.id) === String(roomId));
        setRoom(found);
      })
      .catch(() => setError('Failed to load hotel details'))
      .finally(() => setLoading(false));
  }, [hotelId, roomId]);

  const nights = checkInDate && checkOutDate
    ? Math.max(0, Math.ceil((new Date(checkOutDate) - new Date(checkInDate)) / (1000 * 60 * 60 * 24)))
    : 0;
  const totalAmount = room ? nights * room.pricePerNight : 0;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!checkInDate || !checkOutDate) { setError('Please select check-in and check-out dates'); return; }
    if (nights <= 0) { setError('Check-out must be after check-in'); return; }

    setSubmitting(true);
    try {
      const res = await bookingAPI.create({
        hotelId: Number(hotelId),
        roomId: Number(roomId),
        userId: user?.email,
        checkInDate,
        checkOutDate,
        numberOfGuests,
        totalPrice: totalAmount,
      });
      const bookingId = res.data.id || res.data.bookingId;
      navigate(`/payments/${bookingId}?amount=${totalAmount}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create booking');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="py-20"><LoadingSpinner size="lg" /></div>;

  const today = new Date().toISOString().split('T')[0];

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-12">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Book Your Stay</h1>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      {hotel && room && (
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <div className="flex items-start gap-4 mb-6 pb-6 border-b">
            <div className="h-16 w-16 bg-indigo-100 rounded-xl flex items-center justify-center flex-shrink-0">
              <span className="text-2xl">🏨</span>
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">{hotel.name}</h2>
              <p className="text-gray-500">{room.type} - Room {room.roomNumber}</p>
              <p className="text-indigo-600 font-semibold mt-1">${room.pricePerNight}/night</p>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Check-in Date</label>
                <input type="date" required min={today} value={checkInDate} onChange={(e) => setCheckInDate(e.target.value)} className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Check-out Date</label>
                <input type="date" required min={checkInDate || today} value={checkOutDate} onChange={(e) => setCheckOutDate(e.target.value)} className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none" />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Number of Guests</label>
              <select value={numberOfGuests} onChange={(e) => setNumberOfGuests(Number(e.target.value))} className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none">
                {[1, 2, 3, 4, 5, 6].map((n) => <option key={n} value={n}>{n} {n === 1 ? 'Guest' : 'Guests'}</option>)}
              </select>
            </div>

            <div className="bg-gray-50 rounded-xl p-6 space-y-3">
              <h3 className="font-bold text-gray-900">Price Summary</h3>
              <div className="flex justify-between text-sm text-gray-600">
                <span>${room.pricePerNight} x {nights} {nights === 1 ? 'night' : 'nights'}</span>
                <span>${totalAmount.toFixed(2)}</span>
              </div>
              <div className="flex justify-between font-bold text-lg text-gray-900 pt-2 border-t">
                <span>Total</span>
                <span className="text-indigo-600">${totalAmount.toFixed(2)}</span>
              </div>
            </div>

            <button type="submit" disabled={submitting || nights <= 0} className="w-full bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
              {submitting ? 'Booking...' : 'Confirm Booking'}
            </button>
          </form>
        </div>
      )}
    </div>
  );
}
