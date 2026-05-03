import { useAuth } from "../../context/AuthContext";
import Button from "../ui/Button";
import Badge from "../ui/Badge";

export default function Header() {
  const { user, role, logout } = useAuth();

  return (
    <header className="flex items-center justify-between border-b border-border px-5 py-3 bg-surface">
      <h2>Aaha Telecom</h2>
      <div className="flex items-center gap-3">
        {user && (
          <>
            <span className="text-sm text-textSecondary">{user}</span>
            <Badge label={role} variant="neutral" />
            <Button variant="danger" onClick={logout} className="text-xs px-3 py-1">
              Logout
            </Button>
          </>
        )}
      </div>
    </header>
  );
}
