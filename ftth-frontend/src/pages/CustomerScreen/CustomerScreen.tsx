import { useEffect, useState } from "react";
import { api } from "../../services/apiClient";
import { ENDPOINTS } from "../../services/endpoints";
import {
  inputStyle, primaryBtn, cancelBtn, thStyle, tdStyle,
  focusBorder, blurBorder,
} from "../Users/UsersShared";
import CustomerDetail from "./CustomerDetail";

interface CustomerRow {
  customerId: number;
  customerCode: string;
  fullName: string;
  email: string;
  salary: number;
  status: string;
  pincode: string | null;
}

export default function CustomerScreen() {
  const [customers, setCustomers] = useState<CustomerRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [selected, setSelected] = useState<CustomerRow | null>(null);

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = () => {
    setLoading(true);
    setError("");
    api.get<CustomerRow[]>(ENDPOINTS.CUSTOMERS)
      .then(setCustomers)
      .catch((err) => setError(err instanceof Error ? err.message : "Failed to load customers."))
      .finally(() => setLoading(false));
  };

  const filtered = customers.filter((c) => {
    if (!search.trim()) return true;
    const q = search.toLowerCase();
    return c.customerCode.toLowerCase().includes(q)
      || c.fullName.toLowerCase().includes(q)
      || c.email.toLowerCase().includes(q);
  });

  const statusBadge = (status: string) => {
    const colors: Record<string, { bg: string; text: string }> = {
      ACTIVE: { bg: "#dcfce7", text: "#166534" },
      INACTIVE: { bg: "#f3f4f6", text: "#6b7280" },
      DELETED: { bg: "#fee2e2", text: "#991b1b" },
    };
    const s = colors[status] || colors.INACTIVE;
    return (
      <span style={{ background: s.bg, color: s.text, padding: "2px 8px", borderRadius: "3px", fontSize: "12px", fontWeight: 500 }}>
        {status}
      </span>
    );
  };

  if (selected) {
    return (
      <div style={{ fontFamily: "'Source Sans 3', 'Segoe UI', sans-serif", padding: "24px" }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "20px" }}>
          <h1 style={{ fontSize: "22px", fontWeight: 600, color: "#111827", margin: 0 }}>Customer: {selected.customerCode}</h1>
          <button onClick={() => { setSelected(null); loadCustomers(); }} style={cancelBtn}>← Back to List</button>
        </div>
        <CustomerDetail customerCode={selected.customerCode} />
      </div>
    );
  }

  return (
    <div style={{ fontFamily: "'Source Sans 3', 'Segoe UI', sans-serif", padding: "24px" }}>
      <h1 style={{ fontSize: "22px", fontWeight: 600, color: "#111827", margin: "0 0 20px 0" }}>Customers</h1>

      <div style={{ marginBottom: "12px", maxWidth: "500px" }}>
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by customer code, name, or email..."
          style={{ ...inputStyle, width: "100%" }}
          onFocus={focusBorder}
          onBlur={blurBorder}
        />
      </div>

      <div style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", overflow: "hidden" }}>
        {loading ? (
          <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>Loading customers...</p>
        ) : error ? (
          <p style={{ padding: "16px", fontSize: "13px", color: "#dc2626" }}>{error}</p>
        ) : filtered.length === 0 ? (
          <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>No customers found.</p>
        ) : (
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9" }}>
                {["Code", "Name", "Email", "Salary", "Pincode"].map((h) => (
                  <th key={h} style={thStyle}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filtered.map((c, i) => (
                <tr
                  key={c.customerId}
                  onClick={() => setSelected(c)}
                  style={{ background: i % 2 === 1 ? "#fafafa" : "#ffffff", borderTop: "1px solid #e5e7eb", cursor: "pointer" }}
                >
                  <td style={{ ...tdStyle, fontWeight: 600 }}>{c.customerCode}</td>
                  <td style={tdStyle}>{c.fullName}</td>
                  <td style={{ ...tdStyle, color: "#6b7280" }}>{c.email}</td>
                  <td style={tdStyle}>Rs. {c.salary}</td>
                  <td style={tdStyle}>{c.pincode || "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
