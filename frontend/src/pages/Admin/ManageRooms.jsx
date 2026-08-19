import { useState, useEffect } from 'react';
import { hotelAPI } from '../../services/apiEndpoints';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';

const ROOM_TYPES = ['SINGLE', 'DOUBLE', 'SUITE'];

export default function ManageRooms() {
  const [hotels, setHotels] = useState([]);
  const [selectedHotel, setSelectedHotel] = useState('');
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editRoom, setEditRoom] = useState(null);
  const [form, setForm] = useState({ roomNumber: '', type: 'SINGLE', pricePerNight: '', available: true });

  useEffect(() => {
    hotelAPI.getAll()
      .then((res) => { setHotels(res.data); if (res.data.length > 0) setSelectedHotel(String(res.data[0].id)); })
      .catch(() => setError('Failed to load hotels'))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!selectedHotel) return;
    hotelAPI.getRooms(selectedHotel)
      .then((res) => setRooms(Array.isArray(res.data) ? res.data : []))
      .catch(() => setRooms([]));
  }, [selectedHotel, success]);

  const resetForm = () => { setForm({ roomNumber: '', type: 'SINGLE', pricePerNight: '', available: true }); setEditRoom(null); setShowForm(false); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    const payload = { ...form, pricePerNight: Number(form.pricePerNight) };
    try {
      if (editRoom) {
        await hotelAPI.addRoom(selectedHotel, payload);
        setSuccess('Room updated');
      } else {
        await hotelAPI.addRoom(selectedHotel, payload);
        setSuccess('Room added');
      }
      resetForm();
    } catch (err) {
      setError(err.response?.data?.message || 'Operation failed');
    }
  };

  const handleEdit = (room) => {
    setForm({ roomNumber: room.roomNumber, type: room.type, pricePerNight: room.pricePerNight, available: room.available ?? true });
    setEditRoom(room);
    setShowForm(true);
  };

  const handleDelete = async (hotelId, roomId) => {
    if (!confirm('Delete this room?')) return;
    try {
      await hotelAPI.delete(hotelId);
      setSuccess('Room deleted');
    } catch {
      setError('Failed to delete room');
    }
  };

  if (loading) return <LoadingSpinner size="lg" />;

  return (
    <div>
      {error && <Alert type="error" message={error} onClose={() => setError('')} />}
      {success && <Alert type="success" message={success} onClose={() => setSuccess('')} />}

      <div className="flex flex-col sm:flex-row gap-4 mb-6">
        <select value={selectedHotel} onChange={(e) => setSelectedHotel(e.target.value)} className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none">
          {hotels.map((h) => <option key={h.id} value={h.id}>{h.name}</option>)}
        </select>
        <button onClick={() => { resetForm(); setShowForm(!showForm); }} className="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">
          {showForm ? 'Cancel' : '+ Add Room'}
        </button>
      </div>

      {showForm && (
        <div className="bg-white rounded-xl shadow-md p-6 mb-6 border border-gray-100">
          <h3 className="font-bold text-gray-900 mb-4">{editRoom ? 'Edit Room' : 'Add New Room'}</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Room Number</label>
                <input required value={form.roomNumber} onChange={(e) => setForm({ ...form, roomNumber: e.target.value })} className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none" placeholder="101" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
                <select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })} className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none">
                  {ROOM_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Price/Night ($)</label>
                <input type="number" required min="1" value={form.pricePerNight} onChange={(e) => setForm({ ...form, pricePerNight: e.target.value })} className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none" placeholder="99.00" />
              </div>
            </div>
            <div className="flex items-center gap-2">
              <input type="checkbox" checked={form.available} onChange={(e) => setForm({ ...form, available: e.target.checked })} className="rounded text-indigo-600 focus:ring-indigo-500" />
              <label className="text-sm text-gray-700">Available for booking</label>
            </div>
            <button type="submit" className="bg-indigo-600 text-white px-6 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">{editRoom ? 'Update' : 'Add Room'}</button>
          </form>
        </div>
      )}

      <div className="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase">Room #</th>
              <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase">Type</th>
              <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase">Price</th>
              <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase">Status</th>
              <th className="text-right px-6 py-3 text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {rooms.map((r) => (
              <tr key={r.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 font-medium text-gray-900">{r.roomNumber}</td>
                <td className="px-6 py-4 text-gray-600">{r.type}</td>
                <td className="px-6 py-4 text-gray-600">${r.pricePerNight}</td>
                <td className="px-6 py-4">
                  <span className={`text-xs font-medium px-2 py-1 rounded-full ${r.available !== false ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                    {r.available !== false ? 'Available' : 'Booked'}
                  </span>
                </td>
                <td className="px-6 py-4 text-right space-x-3">
                  <button onClick={() => handleEdit(r)} className="text-indigo-600 hover:underline text-sm font-medium">Edit</button>
                  <button onClick={() => handleDelete(selectedHotel, r.id)} className="text-red-600 hover:underline text-sm font-medium">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {rooms.length === 0 && <p className="text-center py-8 text-gray-500">No rooms yet</p>}
      </div>
    </div>
  );
}
