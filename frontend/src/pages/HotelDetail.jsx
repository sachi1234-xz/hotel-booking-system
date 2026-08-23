import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { hotelAPI } from '../services/apiEndpoints';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { Alert } from '../components/common/Alert';

export default function HotelDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [hotel, setHotel] = useState(null);
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    hotelAPI.getById(id)
      .then((res) => {
        setHotel(res.data);
        setRooms(res.data.rooms || []);
      })
      .catch(() => setError('Failed to load hotel'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleBookRoom = (roomId) => {
    if (!user) {
      navigate('/login');
      return;
    }
    navigate(`/bookings/new?hotelId=${id}&roomId=${roomId}`);
  };

  if (loading) return <div className="py-20"><LoadingSpinner size="lg" /></div>;
  if (error) return <div className="max-w-7xl mx-auto px-4 py-12"><Alert type="error" message={error} /></div>;
  if (!hotel) return <div className="max-w-7xl mx-auto px-4 py-12"><Alert type="error" message="Hotel not found" /></div>;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <Link to="/hotels" className="inline-flex items-center gap-1 text-indigo-600 hover:underline mb-6 text-sm font-medium">
        <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" /></svg>
        Back to Hotels
      </Link>

      <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
        <div className="h-64 bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center">
          <span className="text-8xl text-white/70">🏨</span>
        </div>
        <div className="p-8">
          <h1 className="text-3xl font-bold text-gray-900">{hotel.name}</h1>
          <p className="text-gray-500 mt-2 flex items-center gap-1">
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" /><path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" /></svg>
            {hotel.location}
          </p>
          {hotel.description && <p className="text-gray-600 mt-4 text-lg">{hotel.description}</p>}
        </div>
      </div>

      <div className="mt-10">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Available Rooms</h2>
        {rooms.length === 0 ? (
          <div className="text-center py-12 bg-gray-50 rounded-xl text-gray-500">No rooms available</div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {rooms.map((room) => (
              <div key={room.id} className="bg-white rounded-xl shadow-md border border-gray-100 p-6 flex flex-col">
                <div className="flex items-center justify-between mb-4">
                  <span className="text-sm font-medium bg-indigo-100 text-indigo-700 px-3 py-1 rounded-full">{room.type}</span>
                  {room.available !== false ? (
                    <span className="text-xs font-medium bg-green-100 text-green-700 px-2 py-1 rounded-full">Available</span>
                  ) : (
                    <span className="text-xs font-medium bg-red-100 text-red-700 px-2 py-1 rounded-full">Booked</span>
                  )}
                </div>
                <h3 className="text-lg font-bold text-gray-900">Room {room.roomNumber}</h3>
                <div className="mt-auto pt-4 flex items-center justify-between">
                  <span className="text-2xl font-bold text-indigo-600">${room.pricePerNight}<span className="text-sm font-normal text-gray-500">/night</span></span>
                  {room.available !== false && (
                    <button onClick={() => handleBookRoom(room.id)} className="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">
                      Book Now
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
