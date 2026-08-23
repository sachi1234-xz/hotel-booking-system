import { useState, useEffect } from 'react';
import { authAPI } from '../../services/apiEndpoints';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';

export default function ManageUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = () => {
    setLoading(true);
    authAPI.getAllUsers()
      .then((res) => setUsers(res.data))
      .catch(() => setError('Failed to load users'))
      .finally(() => setLoading(false));
  };

  const handleUpdateRole = async (userId, newRole) => {
    try {
      const user = users.find((u) => u.id === userId);
      await authAPI.updateProfile({ email: user.email, role: newRole });
      fetchUsers();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update role');
    }
  };

  if (loading) return <div className="py-20"><LoadingSpinner size="lg" /></div>;

  return (
    <div>
      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      <div className="bg-white rounded-2xl shadow-md overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
          <div>
            <h3 className="font-bold text-gray-900">All Users</h3>
            <p className="text-sm text-gray-500">{users.length} registered user{users.length !== 1 ? 's' : ''}</p>
          </div>
          <button onClick={fetchUsers} className="text-sm text-indigo-600 hover:underline font-medium">Refresh</button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="text-left px-6 py-3 font-medium">ID</th>
                <th className="text-left px-6 py-3 font-medium">Email</th>
                <th className="text-left px-6 py-3 font-medium">Role</th>
                <th className="text-left px-6 py-3 font-medium">Verified</th>
                <th className="text-left px-6 py-3 font-medium">Joined</th>
                <th className="text-left px-6 py-3 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {users.map((u) => (
                <tr key={u.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-gray-500 font-mono text-xs">{u.id}</td>
                  <td className="px-6 py-4 font-medium text-gray-900">{u.email}</td>
                  <td className="px-6 py-4">
                    <select
                      value={u.role}
                      onChange={(e) => handleUpdateRole(u.id, e.target.value)}
                      className="text-xs font-medium px-2.5 py-1 rounded-full border border-gray-200 focus:ring-2 focus:ring-indigo-500 outline-none"
                    >
                      <option value="USER">USER</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </td>
                  <td className="px-6 py-4">
                    {u.emailVerified ? (
                      <span className="bg-green-100 text-green-700 px-2.5 py-0.5 rounded-full text-xs font-medium">Yes</span>
                    ) : (
                      <span className="bg-yellow-100 text-yellow-700 px-2.5 py-0.5 rounded-full text-xs font-medium">No</span>
                    )}
                  </td>
                  <td className="px-6 py-4 text-gray-500 text-xs">
                    {u.createdAt ? new Date(u.createdAt).toLocaleDateString() : '-'}
                  </td>
                  <td className="px-6 py-4">
                    <button
                      onClick={() => setSelectedUser(u)}
                      className="text-indigo-600 hover:underline text-xs font-medium"
                    >
                      View Details
                    </button>
                  </td>
                </tr>
              ))}
              {users.length === 0 && (
                <tr>
                  <td colSpan="6" className="px-6 py-12 text-center text-gray-500">No users found</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* User Detail Modal */}
      {selectedUser && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={() => setSelectedUser(null)}>
          <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-8" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold text-gray-900">User Details</h3>
              <button onClick={() => setSelectedUser(null)} className="text-gray-400 hover:text-gray-600 text-2xl">&times;</button>
            </div>
            <div className="space-y-4">
              <div className="flex justify-between items-center py-2 border-b border-gray-100">
                <span className="text-gray-500 text-sm">ID</span>
                <span className="font-mono text-sm font-medium">{selectedUser.id}</span>
              </div>
              <div className="flex justify-between items-center py-2 border-b border-gray-100">
                <span className="text-gray-500 text-sm">Email</span>
                <span className="font-medium text-sm">{selectedUser.email}</span>
              </div>
              <div className="flex justify-between items-center py-2 border-b border-gray-100">
                <span className="text-gray-500 text-sm">Role</span>
                <span className={`px-2.5 py-0.5 rounded-full text-xs font-medium ${selectedUser.role === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-indigo-100 text-indigo-700'}`}>
                  {selectedUser.role}
                </span>
              </div>
              <div className="flex justify-between items-center py-2 border-b border-gray-100">
                <span className="text-gray-500 text-sm">Email Verified</span>
                {selectedUser.emailVerified ? (
                  <span className="bg-green-100 text-green-700 px-2.5 py-0.5 rounded-full text-xs font-medium">Yes</span>
                ) : (
                  <span className="bg-yellow-100 text-yellow-700 px-2.5 py-0.5 rounded-full text-xs font-medium">No</span>
                )}
              </div>
              <div className="flex justify-between items-center py-2">
                <span className="text-gray-500 text-sm">Joined</span>
                <span className="text-sm">{selectedUser.createdAt ? new Date(selectedUser.createdAt).toLocaleString() : '-'}</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
