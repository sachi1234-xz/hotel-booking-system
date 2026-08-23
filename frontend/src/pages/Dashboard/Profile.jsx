import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { authAPI } from '../../services/apiEndpoints';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';

export default function Profile() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('profile');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  const [email, setEmail] = useState('');
  const [role, setRole] = useState('');
  const [emailVerified, setEmailVerified] = useState(false);
  const [createdAt, setCreatedAt] = useState('');

  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [verifyCode, setVerifyCode] = useState('');
  const [verifySent, setVerifySent] = useState(false);
  const [sendingCode, setSendingCode] = useState(false);

  useEffect(() => {
    if (user?.email) {
      setEmail(user.email);
      setRole(user.role || 'USER');
      fetchProfile();
    }
  }, [user]);

  const fetchProfile = async () => {
    try {
      const res = await authAPI.getAllUsers();
      const me = res.data.find((u) => u.email === user.email);
      if (me) {
        setRole(me.role);
        setEmailVerified(me.emailVerified);
        setCreatedAt(me.createdAt);
      }
    } catch {
      // fallback - use context data
    }
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      await authAPI.updateProfile({ email, role: user.role });
      setSuccess('Profile updated successfully');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (newPassword !== confirmPassword) {
      setError('New passwords do not match');
      return;
    }
    if (newPassword.length < 6) {
      setError('New password must be at least 6 characters');
      return;
    }

    setLoading(true);
    try {
      await authAPI.changePassword({ currentPassword, newPassword });
      setSuccess('Password changed successfully');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change password');
    } finally {
      setLoading(false);
    }
  };

  const handleSendVerification = async () => {
    setSendingCode(true);
    setError('');
    try {
      await authAPI.sendVerificationCode(user.email);
      setVerifySent(true);
      setSuccess('Verification code sent to your email');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to send verification code');
    } finally {
      setSendingCode(false);
    }
  };

  const handleVerifyEmail = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      await authAPI.verifyEmail({ email: user.email, verificationCode: verifyCode });
      setEmailVerified(true);
      setSuccess('Email verified successfully');
      setVerifyCode('');
      setVerifySent(false);
    } catch (err) {
      setError(err.response?.data?.message || 'Verification failed');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (!confirm('Are you sure you want to delete your account? This cannot be undone.')) return;
    setLoading(true);
    try {
      await authAPI.deleteAccount();
      logout();
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete account');
      setLoading(false);
    }
  };

  const tabs = [
    { key: 'profile', label: 'Profile', icon: '👤' },
    { key: 'password', label: 'Password', icon: '🔒' },
    { key: 'verify', label: 'Email Verification', icon: '✉️' },
    { key: 'danger', label: 'Danger Zone', icon: '⚠️' },
  ];

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 py-12">
      <h1 className="text-3xl font-bold text-gray-900 mb-2">My Profile</h1>
      <p className="text-gray-500 mb-8">Manage your account settings</p>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}
      {success && <Alert type="success" message={success} onClose={() => setSuccess('')} />}

      <div className="flex flex-col sm:flex-row gap-6">
        {/* Sidebar Tabs */}
        <div className="sm:w-56 flex-shrink-0">
          <div className="bg-white rounded-2xl shadow-md p-2 space-y-1">
            {tabs.map((t) => (
              <button
                key={t.key}
                onClick={() => { setActiveTab(t.key); setError(''); setSuccess(''); }}
                className={`w-full text-left px-4 py-3 rounded-xl text-sm font-medium transition-colors flex items-center gap-2 ${
                  activeTab === t.key
                    ? 'bg-indigo-600 text-white'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                <span>{t.icon}</span> {t.label}
              </button>
            ))}
          </div>
        </div>

        {/* Content */}
        <div className="flex-1">
          {/* Profile Tab */}
          {activeTab === 'profile' && (
            <div className="bg-white rounded-2xl shadow-md p-8">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Profile Information</h2>
              <div className="space-y-4 mb-6">
                <div className="flex items-center gap-3 text-sm text-gray-600">
                  <span className="font-medium w-28">Role:</span>
                  <span className="bg-indigo-100 text-indigo-700 px-2.5 py-0.5 rounded-full text-xs font-medium">{role}</span>
                </div>
                <div className="flex items-center gap-3 text-sm text-gray-600">
                  <span className="font-medium w-28">Email Verified:</span>
                  {emailVerified ? (
                    <span className="bg-green-100 text-green-700 px-2.5 py-0.5 rounded-full text-xs font-medium">Verified</span>
                  ) : (
                    <span className="bg-yellow-100 text-yellow-700 px-2.5 py-0.5 rounded-full text-xs font-medium">Not Verified</span>
                  )}
                </div>
                {createdAt && (
                  <div className="flex items-center gap-3 text-sm text-gray-600">
                    <span className="font-medium w-28">Member Since:</span>
                    <span>{new Date(createdAt).toLocaleDateString()}</span>
                  </div>
                )}
              </div>
              <form onSubmit={handleUpdateProfile} className="space-y-5 border-t pt-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading}
                  className="bg-indigo-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50"
                >
                  {loading ? 'Updating...' : 'Update Profile'}
                </button>
              </form>
            </div>
          )}

          {/* Password Tab */}
          {activeTab === 'password' && (
            <div className="bg-white rounded-2xl shadow-md p-8">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Change Password</h2>
              <form onSubmit={handleChangePassword} className="space-y-5">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Current Password</label>
                  <input
                    type="password"
                    required
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
                    placeholder="Enter current password"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">New Password</label>
                  <input
                    type="password"
                    required
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
                    placeholder="Min 6 characters"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Confirm New Password</label>
                  <input
                    type="password"
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
                    placeholder="Re-enter new password"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading}
                  className="bg-indigo-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50"
                >
                  {loading ? 'Changing...' : 'Change Password'}
                </button>
              </form>
            </div>
          )}

          {/* Email Verification Tab */}
          {activeTab === 'verify' && (
            <div className="bg-white rounded-2xl shadow-md p-8">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Email Verification</h2>
              {emailVerified ? (
                <div className="text-center py-8">
                  <div className="text-5xl mb-4">✅</div>
                  <p className="text-lg font-medium text-green-700">Your email is verified</p>
                  <p className="text-gray-500 mt-1">{user.email}</p>
                </div>
              ) : (
                <>
                  <p className="text-gray-600 mb-6">
                    Your email <span className="font-medium">{user.email}</span> is not yet verified.
                  </p>
                  {!verifySent ? (
                    <button
                      onClick={handleSendVerification}
                      disabled={sendingCode}
                      className="bg-indigo-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50"
                    >
                      {sendingCode ? 'Sending...' : 'Send Verification Code'}
                    </button>
                  ) : (
                    <form onSubmit={handleVerifyEmail} className="space-y-5">
                      <p className="text-sm text-green-600 bg-green-50 p-3 rounded-lg">
                        A verification code has been sent. Enter it below.
                      </p>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Verification Code</label>
                        <input
                          type="text"
                          required
                          value={verifyCode}
                          onChange={(e) => setVerifyCode(e.target.value)}
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
                          placeholder="Enter your code"
                        />
                      </div>
                      <div className="flex gap-3">
                        <button
                          type="submit"
                          disabled={loading}
                          className="bg-indigo-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50"
                        >
                          {loading ? 'Verifying...' : 'Verify Email'}
                        </button>
                        <button
                          type="button"
                          onClick={() => { setVerifySent(false); setVerifyCode(''); }}
                          className="bg-gray-100 text-gray-700 px-6 py-3 rounded-lg font-medium hover:bg-gray-200 transition-colors"
                        >
                          Cancel
                        </button>
                      </div>
                    </form>
                  )}
                </>
              )}
            </div>
          )}

          {/* Danger Zone Tab */}
          {activeTab === 'danger' && (
            <div className="bg-white rounded-2xl shadow-md p-8 border-2 border-red-200">
              <h2 className="text-xl font-bold text-red-700 mb-2">Danger Zone</h2>
              <p className="text-gray-600 mb-6">
                Once you delete your account, there is no going back. All your data will be permanently removed.
              </p>
              <div className="bg-red-50 rounded-xl p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div>
                  <p className="font-medium text-red-800">Delete Account</p>
                  <p className="text-sm text-red-600">This action is irreversible</p>
                </div>
                <button
                  onClick={handleDeleteAccount}
                  disabled={loading}
                  className="bg-red-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-red-700 transition-colors disabled:opacity-50"
                >
                  {loading ? 'Deleting...' : 'Delete My Account'}
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
