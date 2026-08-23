import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';
import ProtectedRoute from './components/common/ProtectedRoute';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Hotels from './pages/Hotels';
import HotelDetail from './pages/HotelDetail';
import BookingPage from './pages/BookingPage';
import PaymentPage from './pages/PaymentPage';
import MyBookings from './pages/Dashboard/MyBookings';
import MyPayments from './pages/Dashboard/MyPayments';
import Profile from './pages/Dashboard/Profile';
import AdminDashboard from './pages/Admin/AdminDashboard';
import ResetPassword from './pages/ResetPassword';
import About from './pages/About';
import Contact from './pages/Contact';
import NotFound from './pages/NotFound';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="min-h-screen flex flex-col bg-gray-50">
          <Navbar />
          <main className="flex-1">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/hotels" element={<Hotels />} />
              <Route path="/hotels/:id" element={<HotelDetail />} />
              <Route path="/bookings/new" element={<ProtectedRoute><BookingPage /></ProtectedRoute>} />
              <Route path="/payments/:bookingId" element={<ProtectedRoute><PaymentPage /></ProtectedRoute>} />
              <Route path="/dashboard/bookings" element={<ProtectedRoute><MyBookings /></ProtectedRoute>} />
              <Route path="/dashboard/payments" element={<ProtectedRoute><MyPayments /></ProtectedRoute>} />
              <Route path="/dashboard/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route path="/admin" element={<ProtectedRoute adminOnly><AdminDashboard /></ProtectedRoute>} />
              <Route path="/about" element={<About />} />
              <Route path="/contact" element={<Contact />} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}
