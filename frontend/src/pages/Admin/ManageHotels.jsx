import { useState, useEffect } from 'react';
import { hotelAPI } from '../../services/apiEndpoints';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';

export default function ManageHotels() {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editHotel, setEditHotel] = useState(null);
  const [form, setForm] = useState({ name: '', location: '', description: '' });

  const fetchHotels = () => {
    setLoading(true);
    hotelAPI.getAll()
      .then((res) => setHotels(res.data))
      .catch(() => setError('Failed to load hotels'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchHotels(); }, []);

  const resetForm = () => { setForm({ name: '', location: '', description: '' }); setEditHotel(null); setShowForm(false); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    try {
      if (editHotel) {
        await hotelAPI.update(editHotel.id, form);
        setSuccess('Hotel updated successfully');
      } else {
        await hotelAPI.create(form);
        setSuccess('Hotel created successfully');
      }
      resetForm();
      fetchHotels();
    } catch (err) {
      setError(err.response?.data?.message || 'Operation failed');
    }
  };

  const handleEdit = (hotel) => {
    setForm({ name: hotel.name, location: hotel.location, description: hotel.description || '' });
    setEditHotel(hotel);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this hotel? This cannot be undone.')) return;
    try {
      await hotelAPI.delete(id);
      setSuccess('Hotel deleted');
      fetchHotels();
    } catch {
      setError('Failed to delete hotel');
    }
  };

  if (loading) return <LoadingSpinner size="lg" />;

  return (
    <div>
      {error && <Alert type="error" message={error} onClose={() => setError('')} />}
      {success && <Alert type="success" message={success} onClose={() => setSuccess('')} />}

      <div className="flex justify-end mb-4">
        <button onClick={() => { resetForm(); setShowForm(!showForm); }} className="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">
          {showForm ? 'Cancel' : '+ Add Hotel'}
        </button>
      </div>

      {showForm && (
        <div className="bg-white rounded-xl shadow-md p-6 mb-6 border border-gray-100">
          <h3 className="font-bold text-gray-900 mb-4">{editHotel ? 'Edit Hotel' : 'Add New Hotel'}</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Location</label>
                <input required value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none" />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} rows={3} className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none resize-none" />
            </div>
            <button type="submit" className="bg-indigo-600 text-white px-6 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">{editHotel ? 'Update' : 'Create'}</button>
          </form>
        </div>
      )}

      <div className="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase">Name</th>
              <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase">Location</th>
              <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase">Rooms</th>
              <th className="text-right px-6 py-3 text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {hotels.map((h) => (
              <tr key={h.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 font-medium text-gray-900">{h.name}</td>
                <td className="px-6 py-4 text-gray-600">{h.location}</td>
                <td className="px-6 py-4 text-gray-600">{h.rooms?.length || 0}</td>
                <td className="px-6 py-4 text-right space-x-3">
                  <button onClick={() => handleEdit(h)} className="text-indigo-600 hover:underline text-sm font-medium">Edit</button>
                  <button onClick={() => handleDelete(h.id)} className="text-red-600 hover:underline text-sm font-medium">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {hotels.length === 0 && <p className="text-center py-8 text-gray-500">No hotels yet</p>}
      </div>
    </div>
  );
}
