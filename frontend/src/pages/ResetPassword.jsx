import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authAPI } from '../services/apiEndpoints';
import { Alert } from '../components/common/Alert';

export default function ResetPassword() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [resetCode, setResetCode] = useState('');

  const handleRequestReset = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      const res = await authAPI.requestPasswordReset({ email });
      setResetCode(res.data.code);
      setSuccess('Password reset code has been sent to your email');
      setStep(2);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to request password reset');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (code !== resetCode) {
      setError('Invalid reset code');
      return;
    }
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    if (newPassword.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setLoading(true);
    try {
      await authAPI.changePassword({ currentPassword: 'RESET', newPassword });
      setSuccess('Password reset successfully! Redirecting to login...');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to reset password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-bold text-gray-900">
            {step === 1 ? 'Reset Password' : 'Enter Reset Code'}
          </h2>
          <p className="text-gray-600 mt-2">
            {step === 1 ? 'Enter your email to receive a reset code' : `We sent a code to ${email}`}
          </p>
        </div>

        <div className="bg-white rounded-2xl shadow-lg p-8">
          {error && <Alert type="error" message={error} onClose={() => setError('')} />}
          {success && <Alert type="success" message={success} onClose={() => setSuccess('')} />}

          {/* Step 1: Request Reset */}
          {step === 1 && (
            <form onSubmit={handleRequestReset} className="space-y-5 mt-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
                  placeholder="john@example.com"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50"
              >
                {loading ? 'Sending...' : 'Send Reset Code'}
              </button>
            </form>
          )}

          {/* Step 2: Enter Code + New Password */}
          {step === 2 && (
            <form onSubmit={handleResetPassword} className="space-y-5 mt-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Reset Code</label>
                <input
                  type="text"
                  required
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
                  placeholder="Enter your reset code"
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
                className="w-full bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50"
              >
                {loading ? 'Resetting...' : 'Reset Password'}
              </button>
              <button
                type="button"
                onClick={() => { setStep(1); setResetCode(''); setCode(''); setError(''); setSuccess(''); }}
                className="w-full text-gray-600 hover:text-indigo-600 py-2 text-sm font-medium transition-colors"
              >
                Back to email entry
              </button>
            </form>
          )}

          <p className="text-center mt-6 text-gray-600">
            Remember your password? <Link to="/login" className="text-indigo-600 font-medium hover:underline">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
