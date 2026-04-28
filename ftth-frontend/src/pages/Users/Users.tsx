import { useEffect, useState } from "react";
import { api } from "../../services/apiClient";
import { ENDPOINTS } from "../../services/endpoints";
import { useAuth } from "../../context/AuthContext";

interface UserRow {
  username: string;
  role: string;
  status: string;
}

interface EditState {
  username: string;
  role: string;
  password: string;
}

const ROLES = [
  { value: "CSR", label: "CSR" },
  { value: "MAINT", label: "MAINT" },
];

export default function Users() {
  const { role: currentRole } = useAuth();
  const [users, setUsers] = useState<UserRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showCreate, setShowCreate] = useState(false);
  const [newUser, setNewUser] = useState({ username: "", password: "", role: "CSR" });
  const [createError, setCreateError] = useState("");
  const [createLoading, setCreateLoading] = useState(false);

  const [editUser, setEditUser] = useState<EditState | null>(null);
  const [editError, setEditError] = useState("");
  const [editLoading, setEditLoading] = useState(false);

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

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreateError("");
    setCreateLoading(true);
    try {
      await api.post(ENDPOINTS.USERS, newUser);
      setShowCreate(false);
      setNewUser({ username: "", password: "", role: "CSR" });
      fetchUsers();
    } catch (err: unknown) {
      setCreateError(err instanceof Error ? err.message : "Failed to create user.");
    } finally {
      setCreateLoading(false);
    }
  };

  const handleEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editUser) return;
    setEditError("");
    setEditLoading(true);
    try {
      await api.put(ENDPOINTS.USER_BY_USERNAME(editUser.username), {
        role: editUser.role,
        password: editUser.password,
      });
      setEditUser(null);
      fetchUsers();
    } catch (err: unknown) {
      setEditError(err instanceof Error ? err.message : "Failed to update user.");
    } finally {
      setEditLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.del(ENDPOINTS.USER_BY_USERNAME(deleteTarget));
      setDeleteTarget(null);
      fetchUsers();
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : "Failed to delete user.");
      setDeleteTarget(null);
    }
  };

  return (
    <div style={{ fontFamily: "'Source Sans 3', 'Segoe UI', sans-serif", padding: "24px" }}>
      {/* Header */}
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "20px" }}>
        <h1 style={{ fontSize: "22px", fontWeight: 600, color: "#111827", margin: 0 }}>User Management</h1>
        <button onClick={() => { setShowCreate(true); setCreateError(""); }} style={primaryBtn}>
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
                          <button
                            onClick={() => setEditUser({ username: u.username, role: u.role, password: "" })}
                            style={outlineBtn}
                          >
                            Edit
                          </button>
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

      {/* Create User Modal */}
      {showCreate && (
        <Overlay>
          <div style={modalBox}>
            <h2 style={modalTitle}>Create New User</h2>
            <form onSubmit={handleCreate} style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
              <Field label="Username">
                <input
                  value={newUser.username}
                  onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
                  required
                  placeholder="Enter username"
                  style={inputStyle}
                  onFocus={focusBorder}
                  onBlur={blurBorder}
                />
              </Field>
              <Field label="Password">
                <input
                  type="password"
                  value={newUser.password}
                  onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                  required
                  placeholder="Enter password"
                  style={inputStyle}
                  onFocus={focusBorder}
                  onBlur={blurBorder}
                />
              </Field>
              <Field label="Role">
                <select
                  value={newUser.role}
                  onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                  style={inputStyle}
                >
                  {ROLES.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
                </select>
              </Field>
              {createError && <p style={errText}>{createError}</p>}
              <div style={{ display: "flex", gap: "8px", justifyContent: "flex-end", marginTop: "4px" }}>
                <button type="button" onClick={() => setShowCreate(false)} style={cancelBtn}>Cancel</button>
                <button type="submit" disabled={createLoading} style={primaryBtn}>
                  {createLoading ? "Creating..." : "Create User"}
                </button>
              </div>
            </form>
          </div>
        </Overlay>
      )}

      {/* Edit User Modal */}
      {editUser && (
        <Overlay>
          <div style={modalBox}>
            <h2 style={modalTitle}>Edit User — {editUser.username}</h2>
            <form onSubmit={handleEdit} style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
              <Field label="New Password (leave blank to keep)">
                <input
                  type="password"
                  value={editUser.password}
                  onChange={(e) => setEditUser({ ...editUser, password: e.target.value })}
                  placeholder="New password"
                  style={inputStyle}
                  onFocus={focusBorder}
                  onBlur={blurBorder}
                />
              </Field>
              <Field label="Role">
                <select
                  value={editUser.role}
                  onChange={(e) => setEditUser({ ...editUser, role: e.target.value })}
                  style={inputStyle}
                >
                  {ROLES.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
                </select>
              </Field>
              {editError && <p style={errText}>{editError}</p>}
              <div style={{ display: "flex", gap: "8px", justifyContent: "flex-end", marginTop: "4px" }}>
                <button type="button" onClick={() => setEditUser(null)} style={cancelBtn}>Cancel</button>
                <button type="submit" disabled={editLoading} style={primaryBtn}>
                  {editLoading ? "Saving..." : "Save Changes"}
                </button>
              </div>
            </form>
          </div>
        </Overlay>
      )}

      {/* Delete Confirm Modal */}
      {deleteTarget && (
        <Overlay>
          <div style={modalBox}>
            <h2 style={modalTitle}>Delete User</h2>
            <p style={{ fontSize: "14px", color: "#6b7280", margin: "0 0 20px 0" }}>
              Are you sure you want to delete <strong style={{ color: "#111827" }}>{deleteTarget}</strong>? This cannot be undone.
            </p>
            <div style={{ display: "flex", gap: "8px", justifyContent: "flex-end" }}>
              <button onClick={() => setDeleteTarget(null)} style={cancelBtn}>Cancel</button>
              <button onClick={handleDelete} style={{ ...primaryBtn, background: "#dc2626" }}>Delete</button>
            </div>
          </div>
        </Overlay>
      )}
    </div>
  );
}

function Overlay({ children }: { children: React.ReactNode }) {
  return (
    <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.35)", zIndex: 50, display: "flex", alignItems: "center", justifyContent: "center" }}>
      {children}
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
      <label style={{ fontSize: "13px", color: "#6b7280" }}>{label}</label>
      {children}
    </div>
  );
}

const focusBorder = (e: React.FocusEvent<HTMLInputElement | HTMLSelectElement>) => {
  e.target.style.borderColor = "#256D85";
};
const blurBorder = (e: React.FocusEvent<HTMLInputElement | HTMLSelectElement>) => {
  e.target.style.borderColor = "#d1d5db";
};

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
};

const primaryBtn: React.CSSProperties = {
  height: "36px",
  padding: "0 16px",
  background: "#256D85",
  color: "#ffffff",
  border: "none",
  borderRadius: "3px",
  fontSize: "14px",
  fontWeight: 600,
  cursor: "pointer",
};

const cancelBtn: React.CSSProperties = {
  height: "36px",
  padding: "0 16px",
  background: "#ffffff",
  color: "#111827",
  border: "1px solid #d1d5db",
  borderRadius: "3px",
  fontSize: "14px",
  cursor: "pointer",
};

const outlineBtn: React.CSSProperties = {
  height: "30px",
  padding: "0 12px",
  background: "#ffffff",
  color: "#256D85",
  border: "1px solid #256D85",
  borderRadius: "3px",
  fontSize: "13px",
  cursor: "pointer",
};

const thStyle: React.CSSProperties = {
  padding: "10px 14px",
  fontSize: "13px",
  fontWeight: 600,
  color: "#6b7280",
  textAlign: "left",
};

const tdStyle: React.CSSProperties = {
  padding: "10px 14px",
  fontSize: "14px",
  color: "#111827",
};

const modalBox: React.CSSProperties = {
  background: "#ffffff",
  border: "1px solid #e5e7eb",
  borderRadius: "4px",
  padding: "24px",
  width: "100%",
  maxWidth: "400px",
};

const modalTitle: React.CSSProperties = {
  fontSize: "16px",
  fontWeight: 600,
  color: "#111827",
  margin: "0 0 16px 0",
};

const errText: React.CSSProperties = {
  fontSize: "13px",
  color: "#dc2626",
  margin: 0,
};
