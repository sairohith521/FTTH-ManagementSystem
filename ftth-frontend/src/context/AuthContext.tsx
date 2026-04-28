import { createContext, useContext, useState, type ReactNode } from "react";
import type { Role } from "../types/roles";

interface AuthState {
  user: string | null;
  role: Role;
  isLoggedIn: boolean;
  login: (username: string, role: Role) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<string | null>(() => sessionStorage.getItem("user"));
  const [role, setRole] = useState<Role>(() => (sessionStorage.getItem("role") as Role) || "CSR");

  const login = (username: string, r: Role) => {
    setUser(username);
    setRole(r);
    sessionStorage.setItem("user", username);
    sessionStorage.setItem("role", r);
  };

  const logout = () => {
    setUser(null);
    setRole("CSR");
    sessionStorage.removeItem("user");
    sessionStorage.removeItem("role");
  };

  return (
    <AuthContext.Provider value={{ user, role, isLoggedIn: !!user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthState {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
