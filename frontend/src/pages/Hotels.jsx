import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { hotelAPI } from '../services/apiEndpoints';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { Alert } from '../components/common/Alert';

export default function Hotels() {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');

  useEffect(() => {
    hotelAPI.getAll()
      .then((res) => setHotels(res.data))
      .catch((err) => setError('Failed to load hotels'))
      .finally(() => setLoading(false));
  }, []);

  const filtered = hotels.filter((h) =>
    h.name?.toLowerCase().includes(search.toLowerCase()) ||
    h.location?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <div className="py-20"><LoadingSpinner size="lg" /></div>;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Browse Hotels</h1>
        <div className="relative max-w-md">
          <svg className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
          </svg>
          <input type="text" value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search by name or location..." className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none" />
        </div>
      </div>

      {error && <Alert type="error" message={error} />}

      {filtered.length === 0 ? (
        <div className="text-center py-20 text-gray-500">
          <svg className="h-16 w-16 mx-auto mb-4 text-gray-300" fill="none" viewBox="0 0 24 24" strokeWidth={1} stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 21v-8.25M15.75 21v-8.25M8.25 21v-8.25M3 9l9-6 9 6m-1.5 12V10.332A48.36 48.36 0 0012 9.75c-2.551 0-5.056.2-7.5.582V21M3 21h18M12 6.75h.008v.008H12V6.75z" />
          </svg>
          <p className="text-xl">No hotels found</p>
          <p className="mt-2">Try a different search term</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filtered.map((hotel) => (
            <Link key={hotel.id} to={`/hotels/${hotel.id}`} className="group bg-white rounded-2xl shadow-md hover:shadow-xl transition-all overflow-hidden border border-gray-100">
              <div className="h-48 bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center relative">
                <span className="text-6xl text-white/70">🏨</span>
                {hotel.rooms && (
                  <span className="absolute top-3 right-3 bg-white/90 text-gray-700 text-xs font-medium px-2 py-1 rounded-full">
                    {hotel.rooms.length} rooms
                  </span>
                )}
              </div>
              <div className="p-5">
                <h3 className="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition-colors">{hotel.name}</h3>
                <p className="text-gray-500 text-sm mt-1 flex items-center gap-1">
                  <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" /><path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" /></svg>
                  {hotel.location}
                </p>
                <p className="text-gray-600 text-sm mt-3 line-clamp-2">{hotel.description}</p>
                {hotel.rooms?.length > 0 && (
                  <p className="mt-3 text-indigo-600 text-sm font-medium">
                    From ${Math.min(...hotel.rooms.map(r => r.pricePerNight)).toFixed(0)}/night
                  </p>
                )}
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
