import { useEffect, useState } from "react";
import { inventoryService } from "../../services/inventoryService";
import type { OltInventoryDTO, OltDetail, PortDetail } from "../../types/models";
import Select from "../../components/ui/Select";
import Input from "../../components/ui/Input";
import Loader from "../../components/ui/Loader";

// ── Styles ──
const container: React.CSSProperties = {
  fontFamily: "'Source Sans 3', 'Segoe UI', sans-serif",
  padding: "24px",
  maxWidth: "1200px",
};

const filterBar: React.CSSProperties = {
  display: "flex",
  gap: "16px",
  alignItems: "flex-end",
  padding: "16px 20px",
  background: "#f9fafb",
  border: "1px solid #e5e7eb",
  borderRadius: "10px",
  marginBottom: "24px",
};

const statsGrid: React.CSSProperties = {
  display: "grid",
  gridTemplateColumns: "repeat(auto-fill, minmax(180px, 1fr))",
  gap: "14px",
  marginBottom: "24px",
};

const statCard: React.CSSProperties = {
  background: "#fff",
  border: "1px solid #e5e7eb",
  borderRadius: "10px",
  padding: "18px 16px",
};

const statValue: React.CSSProperties = {
  fontSize: "24px",
  fontWeight: 700,
  color: "#111827",
  margin: "0 0 2px 0",
};

const statLabel: React.CSSProperties = {
  fontSize: "13px",
  color: "#6b7280",
  margin: 0,
};

const sectionTitle: React.CSSProperties = {
  fontSize: "14px",
  fontWeight: 600,
  color: "#6b7280",
  textTransform: "uppercase",
  letterSpacing: "0.5px",
  margin: "24px 0 12px 0",
};

const thStyle: React.CSSProperties = {
  padding: "10px 16px",
  textAlign: "left",
  fontSize: "12px",
  fontWeight: 600,
  letterSpacing: "0.3px",
};

const tdStyle: React.CSSProperties = {
  padding: "10px 16px",
  fontSize: "13px",
};



const badgeStyle = (bg: string, color: string): React.CSSProperties => ({
  display: "inline-block",
  fontSize: "11px",
  fontWeight: 600,
  borderRadius: "4px",
  padding: "3px 10px",
  background: bg,
  color,
});

export default function OltDetails() {
  const [pincodes, setPincodes] = useState<string[]>([]);
  const [pincode, setPincode] = useState("");
  const [olts, setOlts] = useState<OltInventoryDTO[]>([]);
  const [selectedOlt, setSelectedOlt] = useState("");
  const [details, setDetails] = useState<OltDetail | null>(null);
  const [ports, setPorts] = useState<PortDetail[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    inventoryService.getPincodes().then(setPincodes).catch(() => {});
  }, []);

  useEffect(() => {
    if (!pincode || !pincodes.includes(pincode)) {
      setOlts([]); setSelectedOlt(""); setDetails(null); setPorts([]);
      return;
    }
    inventoryService.getOltsByPincode(pincode).then(setOlts).catch(() => {});
  }, [pincode, pincodes]);

  useEffect(() => {
    if (!selectedOlt) { setDetails(null); setPorts([]); return; }
    setLoading(true);
    Promise.all([
      inventoryService.getOltDetails(selectedOlt),
      inventoryService.getOltPorts(selectedOlt),
    ])
      .then(([d, p]) => { setDetails(d); setPorts(p); })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [selectedOlt]);

  // Group ports by splitter
  const splitterMap = new Map<number, PortDetail[]>();
  for (const p of ports) {
    const list = splitterMap.get(p.splitterNumber) || [];
    list.push(p);
    splitterMap.set(p.splitterNumber, list);
  }
  const splitterNumbers = Array.from(splitterMap.keys()).sort((a, b) => a - b);

  const utilizationPercent = details ? Math.round(((details.totalPorts - details.availablePorts) / details.totalPorts) * 100) : 0;

  return (
    <div style={container}>
      {/* Filter Bar */}
      <div style={filterBar}>
        <div style={{ width: "160px" }}>
          <Input
            label="Pincode"
            value={pincode}
            onChange={(e) => { setPincode(e.target.value); setSelectedOlt(""); }}
            placeholder="Enter pincode"
            list="olt-details-pincode-list"
          />
          <datalist id="olt-details-pincode-list">
            {pincodes.map((p) => <option key={p} value={p} />)}
          </datalist>
        </div>
        {olts.length > 0 && (
          <div style={{ width: "200px" }}>
            <Select
              label="Select OLT"
              value={selectedOlt}
              onChange={(e) => setSelectedOlt(e.target.value)}
              options={olts.map((o) => ({ value: o.oltCode, label: `${o.oltCode} (${o.oltType})` }))}
            />
          </div>
        )}
        {!selectedOlt && !loading && (
          <p style={{ fontSize: "13px", color: "#9ca3af", margin: "0 0 0 8px" }}>
            Select a pincode and OLT to view details
          </p>
        )}
      </div>

      {loading && <Loader />}

      {!loading && details && (
        <>
          {/* OLT Header */}
          <div style={{ display: "flex", alignItems: "center", gap: "12px", marginBottom: "20px" }}>
            <h2 style={{ margin: 0, fontSize: "20px", fontWeight: 700, color: "#111827" }}>
              {details.oltCode}
            </h2>
            <span style={badgeStyle("#eff6ff", "#1d4ed8")}>{details.oltType}</span>
            <span style={details.active ? badgeStyle("#f0fdf4", "#166534") : badgeStyle("#fef2f2", "#991b1b")}>
              {details.active ? "\u25CF ACTIVE" : "\u25CF INACTIVE"}
            </span>
          </div>

          {/* Stat Cards */}
          <div style={statsGrid}>
            <div style={statCard}>
              <p style={statValue}>{details.totalPorts}</p>
              <p style={statLabel}>Total Ports</p>
            </div>
            <div style={statCard}>
              <p style={{ ...statValue, color: "#059669" }}>{details.availablePorts}</p>
              <p style={statLabel}>Available Ports</p>
            </div>
            <div style={statCard}>
              <p style={{ ...statValue, color: "#dc2626" }}>{details.totalPorts - details.availablePorts}</p>
              <p style={statLabel}>Used Ports</p>
            </div>
            <div style={statCard}>
              <p style={statValue}>{details.splitters.length}</p>
              <p style={statLabel}>Splitters</p>
            </div>
            <div style={statCard}>
              <p style={{ ...statValue, color: utilizationPercent >= 80 ? "#dc2626" : utilizationPercent >= 50 ? "#f59e0b" : "#059669" }}>
                {utilizationPercent}%
              </p>
              <p style={statLabel}>Utilization</p>
              <ProgressBar percent={utilizationPercent} />
            </div>
          </div>

          {/* Splitter Summary */}
          <p style={sectionTitle}>{"\uD83D\uDCE1"} Splitter Summary</p>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))", gap: "12px", marginBottom: "24px" }}>
            {details.splitters.map((s) => {
              const used = s.totalPorts - s.availablePorts;
              const pct = Math.round((used / s.totalPorts) * 100);
              return (
                <div key={s.splitterNumber} style={{ background: "#fff", border: "1px solid #e5e7eb", borderRadius: "8px", padding: "14px 16px" }}>
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "8px" }}>
                    <span style={{ fontWeight: 600, fontSize: "14px", color: "#111827" }}>
                      Splitter {s.splitterNumber}
                    </span>
                    <span style={{ fontSize: "12px", color: "#6b7280" }}>
                      {s.splitterCode || "\u2014"}
                    </span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px", color: "#6b7280", marginBottom: "6px" }}>
                    <span>{s.availablePorts} available / {s.totalPorts} total</span>
                    <span style={{ fontWeight: 600, color: pct >= 80 ? "#dc2626" : pct >= 50 ? "#f59e0b" : "#059669" }}>{pct}%</span>
                  </div>
                  <ProgressBar percent={pct} />
                </div>
              );
            })}
          </div>

          {/* Port Allocation Tables - 3 per row */}
          {splitterNumbers.length > 0 && (
            <>
              <p style={sectionTitle}>{"\uD83D\uDD0C"} Port Allocation</p>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "16px" }}>
                {splitterNumbers.map((splNum) => {
                  const splPorts = splitterMap.get(splNum) || [];
                  return (
                    <div key={splNum} style={{ background: "#fff", border: "1px solid #e5e7eb", borderRadius: "10px", overflow: "hidden" }}>
                      <div style={{ background: "#1e293b", color: "#fff", padding: "10px 16px", fontSize: "13px", fontWeight: 600 }}>
                        Splitter {splNum}
                      </div>
                      <table style={{ width: "100%", borderCollapse: "collapse" }}>
                        <thead>
                          <tr style={{ background: "#f9fafb" }}>
                            <th style={{ ...thStyle, color: "#6b7280" }}>Port</th>
                            <th style={{ ...thStyle, color: "#6b7280" }}>Customer</th>
                          </tr>
                        </thead>
                        <tbody>
                          {splPorts.map((port) => {
                            const assigned = port.portStatus === "ASSIGNED";
                            return (
                              <tr key={port.portNumber} style={{ borderBottom: "1px solid #f3f4f6" }}>
                                <td style={tdStyle}>
                                  <span style={{ display: "inline-flex", alignItems: "center", justifyContent: "center", width: "24px", height: "24px", borderRadius: "50%", fontSize: "12px", fontWeight: 600, color: "#fff", background: assigned ? "#dc2626" : "#16a34a" }}>
                                    {port.portNumber}
                                  </span>
                                </td>
                                <td style={{ ...tdStyle, fontWeight: port.customerCode ? 600 : 400, color: port.customerCode ? "#111827" : "#9ca3af" }}>
                                  {port.customerCode || "\u2014"}
                                </td>
                              </tr>
                            );
                          })}
                        </tbody>
                      </table>
                    </div>
                  );
                })}
              </div>
            </>
          )}
        </>
      )}
    </div>
  );
}

function ProgressBar({ percent }: { percent: number }) {
  const color = percent >= 80 ? "#dc2626" : percent >= 50 ? "#f59e0b" : "#059669";
  return (
    <div style={{ background: "#e5e7eb", borderRadius: "4px", height: "6px", marginTop: "4px" }}>
      <div style={{ background: color, borderRadius: "4px", height: "6px", width: `${Math.min(percent, 100)}%`, transition: "width 0.3s ease" }} />
    </div>
  );
}
