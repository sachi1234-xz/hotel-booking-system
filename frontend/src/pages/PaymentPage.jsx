import { useState } from 'react';
import { useParams, useSearchParams, useNavigate, Link } from 'react-router-dom';
import { paymentAPI } from '../services/apiEndpoints';
import { useAuth } from '../context/AuthContext';
import { Alert } from '../components/common/Alert';

const METHODS = [
  { value: 'CARD', label: 'Credit/Debit Card', icon: '💳', desc: 'Pay securely with your card' },
  { value: 'PAYPAL', label: 'PayPal', icon: '🅿️', desc: 'Pay with your PayPal account' },
  { value: 'BANK_TRANSFER', label: 'Bank Transfer', icon: '🏦', desc: 'Direct bank transfer' },
];

export default function PaymentPage() {
  const { bookingId } = useParams();
  const [searchParams] = useSearchParams();
  const amount = searchParams.get('amount') || '0';
  const navigate = useNavigate();
  const { user } = useAuth();

  const [method, setMethod] = useState('CARD');
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [invoice, setInvoice] = useState(null);

  const handlePayment = async () => {
    setProcessing(true);
    setError('');
    try {
      const res = await paymentAPI.process({
        bookingId: Number(bookingId),
        userId: user?.email,
        amount: Number(amount),
        currency: 'USD',
        paymentMethod: method,
      });
      setSuccess(true);
      setInvoice(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Payment failed. Please try again.');
    } finally {
      setProcessing(false);
    }
  };

  if (success) {
    return (
      <div className="max-w-lg mx-auto px-4 py-12">
        <div className="bg-white rounded-2xl shadow-lg p-8 text-center">
          <div className="h-20 w-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <svg className="h-10 w-10 text-green-600" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" /></svg>
          </div>
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Payment Successful!</h1>
          <p className="text-gray-600 mb-2">Your booking has been confirmed.</p>
          {invoice?.transactionReference && (
            <p className="text-sm text-gray-500 mb-6">Transaction Ref: {invoice.transactionReference}</p>
          )}
          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <Link to="/dashboard/bookings" className="bg-indigo-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors">View My Bookings</Link>
            <Link to="/hotels" className="bg-gray-100 text-gray-700 px-6 py-3 rounded-lg font-medium hover:bg-gray-200 transition-colors">Browse More Hotels</Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-lg mx-auto px-4 py-12">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Payment</h1>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      <div className="bg-white rounded-2xl shadow-lg p-8">
        <div className="text-center mb-6 pb-6 border-b">
          <p className="text-gray-500 text-sm">Amount to Pay</p>
          <p className="text-4xl font-bold text-indigo-600 mt-1">${Number(amount).toFixed(2)}</p>
        </div>

        <h2 className="font-bold text-gray-900 mb-4">Select Payment Method</h2>
        <div className="space-y-3 mb-8">
          {METHODS.map((m) => (
            <label key={m.value} className={`flex items-center gap-4 p-4 rounded-xl border-2 cursor-pointer transition-all ${method === m.value ? 'border-indigo-500 bg-indigo-50' : 'border-gray-200 hover:border-gray-300'}`}>
              <input type="radio" name="method" value={m.value} checked={method === m.value} onChange={() => setMethod(m.value)} className="text-indigo-600 focus:ring-indigo-500" />
              <span className="text-2xl">{m.icon}</span>
              <div>
                <p className="font-medium text-gray-900">{m.label}</p>
                <p className="text-sm text-gray-500">{m.desc}</p>
              </div>
            </label>
          ))}
        </div>

        <button onClick={handlePayment} disabled={processing} className="w-full bg-indigo-600 text-white py-3 rounded-lg font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50">
          {processing ? 'Processing Payment...' : `Pay $${Number(amount).toFixed(2)}`}
        </button>
        <button onClick={() => navigate(-1)} className="w-full mt-3 bg-gray-100 text-gray-700 py-3 rounded-lg font-medium hover:bg-gray-200 transition-colors">Go Back</button>
      </div>
    </div>
  );
}
