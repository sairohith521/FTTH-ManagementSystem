import { api } from "../../services/apiClient";
import { ENDPOINTS } from "../../services/endpoints";
import { Overlay, primaryBtn, cancelBtn, modalBox, modalTitle } from "./UsersShared";

interface Props {
  username: string;
  onClose: () => void;
  onSuccess: () => void;
}

export default function DeleteUserModal({ username, onClose, onSuccess }: Props) {
  const handleDelete = async () => {
    try {
      await api.del(ENDPOINTS.USER_BY_USERNAME(username));
      onSuccess();
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : "Failed to delete user.");
      onClose();
    }
  };

  return (
    <Overlay>
      <div style={modalBox}>
        <h2 style={modalTitle}>Delete User</h2>
        <p style={{ fontSize: "14px", color: "#6b7280", margin: "0 0 20px 0" }}>
          Are you sure you want to delete <strong style={{ color: "#111827" }}>{username}</strong>? This cannot be undone.
        </p>
        <div style={{ display: "flex", gap: "8px", justifyContent: "flex-end" }}>
          <button onClick={onClose} style={cancelBtn}>Cancel</button>
          <button onClick={handleDelete} style={{ ...primaryBtn, background: "#dc2626" }}>Delete</button>
        </div>
      </div>
    </Overlay>
  );
}
