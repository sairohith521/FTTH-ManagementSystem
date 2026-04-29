import { useEffect, useState } from "react";
import { api } from "../../services/apiClient";
import { ENDPOINTS } from "../../services/endpoints";
import { cancelBtn, errText, thStyle, tdStyle } from "../Users/UsersShared";

interface ActiveConnection {
  connectionId: number;
  customerCode: string;
  fullName: string;
  pincode: string;
  planName: string;
  monthlyPrice: number;
  oltType: string;
  oltCode: string;
  splitterNumber: number;
  portNumber: number;
}

const dangerBtn: React.CSSProperties = {
  height: "36px",
  padding: "0 16px",
  background: "#dc2626",
  color: "#ffffff",
  border: "none",
  borderRadius: "3px",
  fontSize: "14px",
  fontWeight: 600,
  cursor: "pointer",
};

const sectionLabel: React.CSSProperties = {
  fontSize: "13px",
  fontWeight: 600,
  color: "#6b7280",
  margin: "0 0 8px 0",
};

export default function Disconnect({ onBack }: { onBack: () => void }) {
  const [conns, setConns] = useState<ActiveConnection[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [selected, setSelected] = useState<ActiveConnection | null>(null);
  const [dcLoading, setDcLoading] = useState(false);
  const [dcError, setDcError] = useState("");
  const [dcSuccess, setDcSuccess] = useState("");

  // search state
  const [searchBy, setSearchBy] = useState<"name" | "code">("name");
  const [searchText, setSearchText] = useState("");

  useEffect(() => {
    setLoading(true);
    api.get<ActiveConnection[]>(ENDPOINTS.CONNECTION_ACTIVE)
      .then(setConns)
      .catch((err) => setError(err instanceof Error ? err.message : "Failed to load connections."))
      .finally(() => setLoading(false));
  }, []);

  const filtered = conns.filter((c) => {
    const q = searchText.trim().toLowerCase();
    if (!q) return true;
    return searchBy === "name"
      ? c.fullName.toLowerCase().includes(q)
      : c.customerCode.toLowerCase().includes(q);
  });

  const handleDisconnect = async () => {
    if (!selected) return;
    setDcError("");
    setDcSuccess("");
    setDcLoading(true);
    try {
      await api.post(ENDPOINTS.CONNECTION_DISCONNECT(selected.connectionId), {});
      setDcSuccess(`Connection #${selected.connectionId} (${selected.fullName}) disconnected successfully.`);
      setConns((prev) => prev.filter((c) => c.connectionId !== selected.connectionId));
      setSelected(null);
    } catch (err: unknown) {
      setDcError(err instanceof Error ? err.message : "Failed to disconnect.");
    } finally {
      setDcLoading(false);
    }
  };

  return (
    <div style={{ marginTop: "32px" }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "20px" }}>
        <h2 style={{ fontSize: "16px", fontWeight: 600, color: "#111827", margin: 0 }}>Disconnect</h2>
        <button onClick={onBack} style={cancelBtn}>← Back</button>
      </div>

      {/* Search — same style as Change Plan */}
      <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "8px" }}>
        <p style={{ ...sectionLabel, margin: 0 }}>ACTIVE CONNECTIONS — click a row to select</p>
        <div style={{ display: "flex", border: "1px solid #d1d5db", borderRadius: "3px", overflow: "hidden", background: "#ffffff" }}>
          <select
            value={searchBy}
            onChange={(e) => { setSearchBy(e.target.value as "name" | "code"); setSearchText(""); }}
            style={{ height: "30px", border: "none", borderRight: "1px solid #d1d5db", padding: "0 8px", fontSize: "12px", color: "#6b7280", background: "#f9fafb", outline: "none", cursor: "pointer" }}
          >
            <option value="name">Name</option>
            <option value="code">Code</option>
          </select>
          <input
            value={searchText}
            onChange={(e) => { setSearchText(e.target.value); setSelected(null); setDcError(""); setDcSuccess(""); }}
            placeholder={searchBy === "name" ? "Search by name..." : "Search by code..."}
            style={{ height: "30px", border: "none", padding: "0 10px", fontSize: "13px", outline: "none", width: "180px" }}
          />
        </div>
      </div>

      <div style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", overflow: "hidden", marginBottom: "24px" }}>
        {loading ? (
          <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>Loading connections...</p>
        ) : error ? (
          <p style={{ padding: "16px", fontSize: "13px", color: "#dc2626" }}>{error}</p>
        ) : filtered.length === 0 ? (
          <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>
            {searchText ? "No connections match your search." : "No active connections found."}
          </p>
        ) : (
          <div style={{ overflowY: "auto", maxHeight: "308px" }}>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead style={{ position: "sticky", top: 0, zIndex: 1 }}>
                <tr style={{ background: "#f1f5f9" }}>
                  {["Conn ID", "Cust Code", "Customer", "Pincode", "Plan", "Price", "OLT", "Port"].map((h) => (
                    <th key={h} style={thStyle}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map((c, i) => {
                  const sel = selected?.connectionId === c.connectionId;
                  return (
                    <tr
                      key={c.connectionId}
                      onClick={() => { setSelected(c); setDcError(""); setDcSuccess(""); }}
                      style={{ background: sel ? "#fee2e2" : i % 2 === 1 ? "#fafafa" : "#ffffff", borderTop: "1px solid #e5e7eb", cursor: "pointer" }}
                    >
                      <td style={tdStyle}>{c.connectionId}</td>
                      <td style={tdStyle}>{c.customerCode}</td>
                      <td style={{ ...tdStyle, fontWeight: sel ? 600 : 400 }}>{c.fullName}</td>
                      <td style={tdStyle}>{c.pincode}</td>
                      <td style={tdStyle}>{c.planName}</td>
                      <td style={{ ...tdStyle, color: "#256D85", fontWeight: 500 }}>Rs. {c.monthlyPrice}</td>
                      <td style={tdStyle}>{c.oltType}</td>
                      <td style={{ ...tdStyle, fontSize: "12px", color: "#6b7280" }}>{c.oltCode}/Spl{c.splitterNumber}/Port{c.portNumber}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {selected && (
        <>
          <div style={{ background: "#fef9c3", border: "1px solid #fde68a", borderRadius: "4px", padding: "12px 16px", marginBottom: "16px" }}>
            <p style={{ fontSize: "13px", color: "#92400e", margin: 0 }}>
              ⚠ You are about to disconnect: <strong>{selected.fullName}</strong> ({selected.customerCode}) —{" "}
              <strong>{selected.planName}</strong> @ Rs.{selected.monthlyPrice}
              <br />
              Port: {selected.oltCode}/Spl{selected.splitterNumber}/Port{selected.portNumber}
              <br />
              This will release the port and deactivate the customer.
            </p>
          </div>

          {dcError && <p style={{ ...errText, marginBottom: "12px" }}>{dcError}</p>}

          <div style={{ display: "flex", gap: "8px" }}>
            <button onClick={() => { setSelected(null); setDcError(""); }} style={cancelBtn}>Cancel</button>
            <button onClick={handleDisconnect} disabled={dcLoading} style={dangerBtn}>
              {dcLoading ? "Disconnecting..." : "Confirm Disconnect"}
            </button>
          </div>
        </>
      )}

      {dcSuccess && <p style={{ fontSize: "13px", color: "#16a34a", marginTop: "12px" }}>{dcSuccess}</p>}
    </div>
  );
}
