import { useState } from "react";
import { api } from "../../services/apiClient";
import { ENDPOINTS } from "../../services/endpoints";
import { Overlay, Field, inputStyle, primaryBtn, cancelBtn, modalBox, modalTitle, errText, focusBorder, blurBorder } from "./UsersShared";

const ROLES = [
  { value: "CSR", label: "CSR" },
  { value: "MAINT", label: "MAINT" },
];

interface Props {
  username: string;
  currentRole: string;
  onClose: () => void;
  onSuccess: () => void;
}

export default function EditUserModal({ username, currentRole, onClose, onSuccess }: Props) {
  const [editUser, setEditUser] = useState({ role: currentRole, password: "" });
  const [editError, setEditError] = useState("");
  const [editLoading, setEditLoading] = useState(false);

  const handleEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    setEditError("");
    setEditLoading(true);
    try {
      await api.put(ENDPOINTS.USER_BY_USERNAME(username), {
        role: editUser.role,
        password: editUser.password,
      });
      onSuccess();
    } catch (err: unknown) {
      setEditError(err instanceof Error ? err.message : "Failed to update user.");
    } finally {
      setEditLoading(false);
    }
  };

  return (
    <Overlay>
      <div style={modalBox}>
        <h2 style={modalTitle}>Edit User — {username}</h2>
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
            <button type="button" onClick={onClose} style={cancelBtn}>Cancel</button>
            <button type="submit" disabled={editLoading} style={primaryBtn}>
              {editLoading ? "Saving..." : "Save Changes"}
            </button>
          </div>
        </form>
      </div>
    </Overlay>
  );
}
