import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { authService } from "../../services/authService";
import type { Role } from "../../types/roles";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await authService.login(username, password);
      login(res.username, res.role as Role);
      navigate("/dashboard");
    } catch {
      setError("Invalid credentials. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: "100vh", background: "#f4f6f8", display: "flex", alignItems: "center", justifyContent: "center", fontFamily: "'Source Sans 3', 'Segoe UI', sans-serif" }}>
      <div style={{ background: "#ffffff", border: "1px solid #e5e7eb", borderRadius: "4px", padding: "18px", width: "100%", maxWidth: "360px" }}>
        <h1 style={{ fontSize: "22px", fontWeight: 600, color: "#111827", margin: "0 0 4px 0" }}>FTTH Portal</h1>
        <p style={{ fontSize: "14px", color: "#6b7280", margin: "0 0 20px 0" }}>Sign in to your account</p>

        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "14px" }}>
          <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
            <label style={{ fontSize: "13px", fontWeight: 400, color: "#6b7280" }}>Username</label>
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              placeholder="Enter username"
              style={inputStyle}
              onFocus={(e) => { e.target.style.borderColor = "#256D85"; }}
              onBlur={(e) => { e.target.style.borderColor = "#d1d5db"; }}
            />
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
            <label style={{ fontSize: "13px", fontWeight: 400, color: "#6b7280" }}>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="Enter password"
              style={inputStyle}
              onFocus={(e) => { e.target.style.borderColor = "#256D85"; }}
              onBlur={(e) => { e.target.style.borderColor = "#d1d5db"; }}
            />
          </div>

          {error && <p style={{ fontSize: "13px", color: "#dc2626", margin: 0 }}>{error}</p>}

          <button type="submit" disabled={loading} style={btnStyle}>
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>
      </div>
    </div>
  );
}

const inputStyle: React.CSSProperties = {
  height: "36px",
  border: "1px solid #d1d5db",
  borderRadius: "3px",
  padding: "0 10px",
  fontSize: "13px",
  color: "#111827",
  background: "#ffffff",
  outline: "none",
  boxShadow: "none",
  transition: "border-color 0.15s",
};

const btnStyle: React.CSSProperties = {
  width: "100%",
  height: "36px",
  background: "#256D85",
  color: "#ffffff",
  border: "none",
  borderRadius: "3px",
  fontSize: "14px",
  fontWeight: 600,
  cursor: "pointer",
  opacity: 1,
};
