import { useState, useEffect } from 'react';
import { paymentAPI } from '../../services/apiEndpoints';
import { useAuth } from '../../context/AuthContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';

const STATUS_STYLES = {
  COMPLETED: 'bg-green-100 text-green-700',
  PENDING: 'bg-yellow-100 text-yellow-700',
  FAILED: 'bg-red-100 text-red-700',
  REFUNDED: 'bg-blue-100 text-blue-700',
};

const METHOD_ICONS = { CARD: '💳', PAYPAL: '🅿️', BANK_TRANSFER: '🏦' };

export default function MyPayments() {
  const { user } = useAuth();
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [viewingInvoice, setViewingInvoice] = useState(null);

  useEffect(() => {
    if (!user?.email) return;
    paymentAPI.getMyPayments()
      .then((res) => setPayments(res.data))
      .catch(() => setError('Failed to load payment history'))
      .finally(() => setLoading(false));
  }, [user]);

  const handleViewInvoice = async (id) => {
    try {
      const res = await paymentAPI.getInvoice(id);
      setViewingInvoice(res.data);
    } catch {
      setError('Failed to load invoice');
    }
  };

  if (loading) return <div className="py-20"><LoadingSpinner size="lg" /></div>;

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 py-12">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Payment History</h1>
      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      {viewingInvoice && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={() => setViewingInvoice(null)}>
          <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-8" onClick={(e) => e.stopPropagation()}>
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold text-gray-900">Invoice</h2>
              <button onClick={() => setViewingInvoice(null)} className="text-gray-400 hover:text-gray-600 text-2xl">&times;</button>
            </div>
            <div className="space-y-3 text-sm">
              {Object.entries(viewingInvoice).map(([key, val]) => (
                <div key={key} className="flex justify-between">
                  <span className="text-gray-500 capitalize">{key.replace(/([A-Z])/g, ' $1')}</span>
                  <span className="font-medium text-gray-900">{String(val)}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {payments.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl shadow">
          <p className="text-gray-500 text-lg">No payments yet.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {payments.map((p) => (
            <div key={p.id} className="bg-white rounded-xl shadow-md border border-gray-100 p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div className="flex items-center gap-4">
                <span className="text-3xl">{METHOD_ICONS[p.paymentMethod] || '💳'}</span>
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="font-bold text-gray-900">Payment #{p.id}</h3>
                    <span className={`text-xs font-medium px-2.5 py-0.5 rounded-full ${STATUS_STYLES[p.status] || 'bg-gray-100 text-gray-700'}`}>{p.status}</span>
                  </div>
                  <p className="text-sm text-gray-500">Booking #{p.bookingId} &middot; {p.paymentMethod} &middot; {p.currency}</p>
                  {p.transactionReference && <p className="text-xs text-gray-400 mt-1">Ref: {p.transactionReference}</p>}
                </div>
              </div>
              <div className="flex items-center gap-4">
                <span className="text-xl font-bold text-gray-900">${p.amount?.toFixed(2)}</span>
                <button onClick={() => handleViewInvoice(p.id)} className="text-sm text-indigo-600 hover:underline font-medium">Invoice</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
