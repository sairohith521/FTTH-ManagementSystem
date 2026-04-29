import { useEffect, useState } from "react";
import { api } from "../../services/apiClient";
import { ENDPOINTS } from "../../services/endpoints";
import { useAuth } from "../../context/AuthContext";
import { primaryBtn, outlineBtn, thStyle, tdStyle } from "./UsersShared";
import CreateUserForm from "./CreateUserForm";
import EditUserModal from "./EditUserModal";
import DeleteUserModal from "./DeleteUserModal";

interface UserRow {
  username: string;
  role: string;
  status: string;
}

export default function Users() {
  const { role: currentRole } = useAuth();
  const [users, setUsers] = useState<UserRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showCreate, setShowCreate] = useState(false);
  const [editTarget, setEditTarget] = useState<UserRow | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<string | null>(null);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const data = await api.get<UserRow[]>(ENDPOINTS.USERS);
      setUsers(data);
    } catch {
      setError("Failed to load users.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchUsers(); }, []);

  return (
    <div style={{ fontFamily: "'Source Sans 3', 'Segoe UI', sans-serif", padding: "24px" }}>
      {/* Header */}
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "20px" }}>
        <h1 style={{ fontSize: "22px", fontWeight: 600, color: "#111827", margin: 0 }}>User Management</h1>
        <button onClick={() => setShowCreate(true)} style={primaryBtn}>
          + Create New User
        </button>
      </div>

      {/* Table */}
      <div style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", overflow: "hidden" }}>
        {loading ? (
          <p style={{ padding: "20px", color: "#6b7280", fontSize: "14px" }}>Loading...</p>
        ) : error ? (
          <p style={{ padding: "20px", color: "#dc2626", fontSize: "14px" }}>{error}</p>
        ) : (
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9" }}>
                {["Username", "Role", "Status", "Action"].map((h) => (
                  <th key={h} style={thStyle}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr>
                  <td colSpan={4} style={{ padding: "16px", textAlign: "center", color: "#6b7280", fontSize: "14px" }}>
                    No users found.
                  </td>
                </tr>
              ) : (
                users.map((u, i) => (
                  <tr key={u.username} style={{ background: i % 2 === 1 ? "#fafafa" : "#ffffff", borderTop: "1px solid #e5e7eb" }}>
                    <td style={tdStyle}>{u.username}</td>
                    <td style={tdStyle}>{u.role}</td>
                    <td style={tdStyle}>
                      <span style={{ color: "#16a34a", fontSize: "13px", fontWeight: 500 }}>{u.status}</span>
                    </td>
                    <td style={tdStyle}>
                      {u.role !== "ADMIN" && currentRole === "ADMIN" ? (
                        <>
                          <button onClick={() => setEditTarget(u)} style={outlineBtn}>Edit</button>
                          <button
                            onClick={() => setDeleteTarget(u.username)}
                            style={{ ...outlineBtn, color: "#dc2626", borderColor: "#dc2626", marginLeft: "8px" }}
                          >
                            Delete
                          </button>
                        </>
                      ) : (
                        <span style={{ fontSize: "13px", color: "#9ca3af" }}>—</span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>

      {showCreate && (
        <CreateUserForm
          onClose={() => setShowCreate(false)}
          onSuccess={() => { setShowCreate(false); fetchUsers(); }}
        />
      )}

      {editTarget && (
        <EditUserModal
          username={editTarget.username}
          currentRole={editTarget.role}
          onClose={() => setEditTarget(null)}
          onSuccess={() => { setEditTarget(null); fetchUsers(); }}
        />
      )}

      {deleteTarget && (
        <DeleteUserModal
          username={deleteTarget}
          onClose={() => setDeleteTarget(null)}
          onSuccess={() => { setDeleteTarget(null); fetchUsers(); }}
        />
      )}
    </div>
  );
}
