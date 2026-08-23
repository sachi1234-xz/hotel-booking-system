import { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/apiEndpoints';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      authAPI.validate()
        .then((res) => {
          if (!res.data.valid) logout();
        })
        .catch(() => logout());
    }
  }, []);

  const login = async (email, password) => {
    const res = await authAPI.login({ email, password });
    const { token, email: userEmail, role } = res.data;
    localStorage.setItem('token', token);
    const userData = { email: userEmail, role };
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    return res.data;
  };

  const register = async (email, password) => {
    const res = await authAPI.register({ email, password });
    const { token, email: userEmail, role } = res.data;
    localStorage.setItem('token', token);
    const userData = { email: userEmail, role };
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    return res.data;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading, setLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
