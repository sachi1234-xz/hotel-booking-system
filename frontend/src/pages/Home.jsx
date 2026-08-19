import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { hotelAPI } from '../services/apiEndpoints';

export default function Home() {
  const [hotels, setHotels] = useState([]);

  useEffect(() => {
    hotelAPI.getAll().then((res) => setHotels(res.data.slice(0, 2))).catch(() => {});
  }, []);

  return (
    <div>
      <section className="relative bg-gradient-to-r from-indigo-600 to-purple-600 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
          <div className="max-w-2xl">
            <h1 className="text-4xl md:text-5xl font-bold mb-6">Find Your Perfect Stay</h1>
            <p className="text-xl text-indigo-100 mb-8">Discover handpicked hotels and resorts for unforgettable experiences. Book your dream getaway today.</p>
            <Link to="/hotels" className="inline-block bg-white text-indigo-600 font-semibold px-8 py-3 rounded-lg hover:bg-indigo-50 transition-colors text-lg">Browse Hotels</Link>
          </div>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
          {[
            { icon: '🏨', title: '500+ Hotels', desc: 'Premium hotels worldwide' },
            { icon: '⭐', title: '4.9 Rating', desc: 'Trusted by thousands' },
            { icon: '🔒', title: 'Secure Booking', desc: 'Safe & encrypted payments' },
          ].map((item, i) => (
            <div key={i} className="p-8 rounded-2xl bg-gray-50 hover:shadow-lg transition-shadow">
              <div className="text-4xl mb-4">{item.icon}</div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">{item.title}</h3>
              <p className="text-gray-600">{item.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {hotels.length > 0 && (
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">Featured Hotels</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {hotels.map((hotel) => (
              <Link key={hotel.id} to={`/hotels/${hotel.id}`} className="group bg-white rounded-2xl shadow-md hover:shadow-xl transition-all overflow-hidden">
                <div className="h-48 bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center">
                  <span className="text-6xl text-white/80">🏨</span>
                </div>
                <div className="p-6">
                  <h3 className="text-xl font-bold text-gray-900 group-hover:text-indigo-600 transition-colors">{hotel.name}</h3>
                  <p className="text-gray-500 mt-1 flex items-center gap-1">
                    <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" /><path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" /></svg>
                    {hotel.location}
                  </p>
                  <p className="text-gray-600 mt-3 line-clamp-2">{hotel.description}</p>
                  {hotel.rooms && (
                    <p className="mt-4 text-sm text-indigo-600 font-medium">{hotel.rooms.length} rooms available</p>
                  )}
                </div>
              </Link>
            ))}
          </div>
          <div className="text-center mt-8">
            <Link to="/hotels" className="inline-block bg-indigo-600 text-white px-6 py-3 rounded-lg hover:bg-indigo-700 transition-colors font-medium">View All Hotels</Link>
          </div>
        </section>
      )}

      <section className="bg-indigo-600 text-white py-16">
        <div className="max-w-4xl mx-auto px-4 text-center">
          <h2 className="text-3xl font-bold mb-4">Ready to Book Your Stay?</h2>
          <p className="text-indigo-100 mb-8 text-lg">Join thousands of happy travelers. Create an account and start booking today.</p>
          <Link to="/register" className="inline-block bg-white text-indigo-600 font-semibold px-8 py-3 rounded-lg hover:bg-indigo-50 transition-colors text-lg">Get Started Free</Link>
        </div>
      </section>
    </div>
  );
}
