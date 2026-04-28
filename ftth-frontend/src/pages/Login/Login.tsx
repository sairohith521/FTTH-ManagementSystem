import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { authService } from "../../services/authService";
import type { Role } from "../../types/roles";
import Button from "../../components/ui/Button";
import Input from "../../components/ui/Input";
import Card from "../../components/ui/Card";

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
      setError("Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-background">
      <Card className="w-full max-w-sm">
        <h1 className="mb-1">Aaha Telecom</h1>
        <p className="text-textSecondary mb-5">Sign in to continue</p>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <Input
            label="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <Input
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          {error && <p className="text-error">{error}</p>}

          <Button type="submit" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </Button>
        </form>
      </Card>
    </div>
  );
}
