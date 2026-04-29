import { useEffect, useState } from "react";
import { api } from "../../services/apiClient";
import { ENDPOINTS } from "../../services/endpoints";
import {
  Field, inputStyle, primaryBtn, cancelBtn, errText,
  focusBorder, blurBorder, thStyle, tdStyle,
} from "../Users/UsersShared";
import Disconnect from "./Disconnect";

interface Plan {
  planId: number;
  planName: string;
  speedLabel: string;
  dataLimitLabel: string;
  ottCount: number;
  monthlyPrice: number;
  oltType: string;
}

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
  planId: number;
  serviceAreaId: number;
}

type View = "menu" | "new-install" | "change" | "disconnect";

const cardStyle: React.CSSProperties = {
  background: "#ffffff",
  border: "1px solid #d1d5db",
  borderRadius: "4px",
  padding: "24px 20px",
  width: "200px",
  textAlign: "center",
};

const sectionLabel: React.CSSProperties = {
  fontSize: "13px",
  fontWeight: 600,
  color: "#6b7280",
  margin: "0 0 8px 0",
};

export default function Connections() {
  const [view, setView] = useState<View>("menu");

  // ── New Install state ──
  const [plans, setPlans] = useState<Plan[]>([]);
  const [pincodes, setPincodes] = useState<string[]>([]);
  const [selectedPlanId, setSelectedPlanId] = useState<number | null>(null);
  const [niPlanSearch, setNiPlanSearch] = useState("");
  const [niForm, setNiForm] = useState({ customerName: "", email: "", salary: "", pincode: "" });
  const [niLoading, setNiLoading] = useState(false);
  const [niDataLoading, setNiDataLoading] = useState(false);
  const [niDataError, setNiDataError] = useState("");
  const [niError, setNiError] = useState("");
  const [niSuccess, setNiSuccess] = useState("");

  // ── Change Plan state ──
  const [activeConns, setActiveConns] = useState<ActiveConnection[]>([]);
  const [connsLoading, setConnsLoading] = useState(false);
  const [connsError, setConnsError] = useState("");
  const [selectedConn, setSelectedConn] = useState<ActiveConnection | null>(null);
  const [cpConnSearch, setCpConnSearch] = useState("");
  const [cpConnSearchBy, setCpConnSearchBy] = useState<"name" | "code">("name");
  const [availPlans, setAvailPlans] = useState<Plan[]>([]);
  const [availPlansLoading, setAvailPlansLoading] = useState(false);
  const [cpPlanSearch, setCpPlanSearch] = useState("");
  const [selectedNewPlanId, setSelectedNewPlanId] = useState<number | null>(null);
  const [cpLoading, setCpLoading] = useState(false);
  const [cpError, setCpError] = useState("");
  const [cpSuccess, setCpSuccess] = useState("");

  // ── Load data on view change ──
  useEffect(() => {
    if (view === "new-install") {
      setNiDataLoading(true);
      setNiDataError("");
      Promise.all([
        api.get<Plan[]>(ENDPOINTS.PLANS),
        api.get<string[]>(ENDPOINTS.INVENTORY_PINCODES),
      ])
        .then(([p, pins]) => { setPlans(p); setPincodes(pins); })
        .catch((err) => setNiDataError(err instanceof Error ? err.message : "Failed to load data."))
        .finally(() => setNiDataLoading(false));
    }

    if (view === "change") {
      setConnsLoading(true);
      setConnsError("");
      setSelectedConn(null);
      setAvailPlans([]);
      setSelectedNewPlanId(null);
      setCpConnSearch("");
      setCpPlanSearch("");
      api.get<ActiveConnection[]>(ENDPOINTS.CONNECTION_ACTIVE)
        .then(setActiveConns)
        .catch((err) => setConnsError(err instanceof Error ? err.message : "Failed to load connections."))
        .finally(() => setConnsLoading(false));
    }
  }, [view]);

  // ── Load available plans when a connection is selected ──
  useEffect(() => {
    if (!selectedConn) return;
    setAvailPlansLoading(true);
    setAvailPlans([]);
    setSelectedNewPlanId(null);
    setCpError("");
    setCpSuccess("");
    api.get<Plan[]>(ENDPOINTS.CONNECTION_AVAILABLE_PLANS(selectedConn.connectionId))
      .then(setAvailPlans)
      .catch((err) => setCpError(err instanceof Error ? err.message : "Failed to load plans."))
      .finally(() => setAvailPlansLoading(false));
  }, [selectedConn]);

  const resetNi = () => {
    setNiForm({ customerName: "", email: "", salary: "", pincode: "" });
    setSelectedPlanId(null);
    setNiPlanSearch("");
    setNiError("");
  };

  const handleNewInstall = async (e: React.FormEvent) => {
    e.preventDefault();
    setNiError("");
    setNiSuccess("");
    const plan = plans.find((p) => p.planId === selectedPlanId);
    if (!plan) { setNiError("Please select a plan from the table."); return; }
    setNiLoading(true);
    try {
      await api.post(ENDPOINTS.CONNECTION_NEW_INSTALL, {
        customerName: niForm.customerName,
        email: niForm.email,
        salary: Number(niForm.salary),
        pincode: Number(niForm.pincode),
        planId: plan.planId,
        oltType: plan.oltType,
      });
      resetNi();
      setNiSuccess("Connection created successfully.");
    } catch (err: unknown) {
      setNiError(err instanceof Error ? err.message : "Failed to create connection.");
    } finally {
      setNiLoading(false);
    }
  };

  const handleChangePlan = async () => {
    if (!selectedConn || !selectedNewPlanId) return;
    setCpError("");
    setCpSuccess("");
    setCpLoading(true);
    try {
      await api.post(ENDPOINTS.CONNECTION_CHANGE_PLAN(selectedConn.connectionId), { planId: selectedNewPlanId });
      setCpSuccess("Plan changed successfully.");
      const updated = await api.get<ActiveConnection[]>(ENDPOINTS.CONNECTION_ACTIVE);
      setActiveConns(updated);
      setSelectedConn(null);
      setAvailPlans([]);
      setSelectedNewPlanId(null);
      setCpPlanSearch("");
    } catch (err: unknown) {
      setCpError(err instanceof Error ? err.message : "Failed to change plan.");
    } finally {
      setCpLoading(false);
    }
  };

  const selectedNewPlan = availPlans.find((p) => p.planId === selectedNewPlanId) ?? null;

  const goBack = () => {
    setView("menu");
    resetNi();
    setNiSuccess("");
    setCpError("");
    setCpSuccess("");
    setSelectedConn(null);
  };

  return (
    <div style={{ fontFamily: "'Source Sans 3', 'Segoe UI', sans-serif", padding: "24px" }}>
      <h1 style={{ fontSize: "22px", fontWeight: 600, color: "#111827", margin: "0 0 24px 0" }}>Connections</h1>

      {/* 4 Option Cards */}
      <div style={{ display: "flex", gap: "16px", flexWrap: "wrap" }}>
        {[
          { key: "new-install", label: "New Install",  desc: "Add a new customer connection",    active: true },
          { key: "move",        label: "Move",         desc: "Move customer to new pincode",      active: false },
          { key: "change",      label: "Change Plan",  desc: "Update customer service plan",      active: true },
          { key: "disconnect",  label: "Disconnect",   desc: "Terminate customer connection",     active: true },
        ].map((opt) => (
          <div
            key={opt.key}
            onClick={() => opt.active && setView(opt.key as View)}
            style={{
              ...cardStyle,
              borderColor: view === opt.key ? "#256D85" : "#d1d5db",
              cursor: opt.active ? "pointer" : "not-allowed",
              opacity: opt.active ? 1 : 0.45,
            }}
          >
            <p style={{ fontSize: "15px", fontWeight: 600, color: "#111827", margin: "0 0 6px 0" }}>{opt.label}</p>
            <p style={{ fontSize: "13px", color: "#6b7280", margin: 0 }}>{opt.desc}</p>
          </div>
        ))}
      </div>

      {/* ── NEW INSTALL ── */}
      {view === "new-install" && (
        <div style={{ marginTop: "32px" }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "20px" }}>
            <h2 style={{ fontSize: "16px", fontWeight: 600, color: "#111827", margin: 0 }}>New Install</h2>
            <button onClick={goBack} style={cancelBtn}>← Back</button>
          </div>

          {/* Plan search */}
          <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "8px" }}>
            <p style={{ ...sectionLabel, margin: 0 }}>AVAILABLE PLANS — click a row to select</p>
            <input
              value={niPlanSearch}
              onChange={(e) => setNiPlanSearch(e.target.value)}
              placeholder="Search plan by name..."
              style={{ height: "30px", border: "1px solid #d1d5db", borderRadius: "3px", padding: "0 10px", fontSize: "13px", outline: "none", width: "200px" }}
            />
          </div>
          <div style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", overflow: "hidden", marginBottom: "24px" }}>
            {niDataLoading ? (
              <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>Loading plans...</p>
            ) : niDataError ? (
              <p style={{ padding: "16px", fontSize: "13px", color: "#dc2626" }}>{niDataError}</p>
            ) : (
              <div style={{ overflowY: "auto", maxHeight: "220px" }}>
                <table style={{ width: "100%", borderCollapse: "collapse" }}>
                  <thead style={{ position: "sticky", top: 0, zIndex: 1 }}>
                    <tr style={{ background: "#f1f5f9" }}>
                      {["ID", "Plan Name", "Speed", "Data", "OTTs", "OLT Type", "Price / Month"].map((h) => (
                        <th key={h} style={thStyle}>{h}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {plans
                      .filter((p) => p.planName.toLowerCase().includes(niPlanSearch.toLowerCase()))
                      .map((p, i) => {
                        const sel = selectedPlanId === p.planId;
                        return (
                          <tr key={p.planId} onClick={() => setSelectedPlanId(p.planId)}
                            style={{ background: sel ? "#e0f2fe" : i % 2 === 1 ? "#fafafa" : "#ffffff", borderTop: "1px solid #e5e7eb", cursor: "pointer" }}>
                            <td style={tdStyle}>{p.planId}</td>
                            <td style={{ ...tdStyle, fontWeight: sel ? 600 : 400 }}>{p.planName}</td>
                            <td style={tdStyle}>{p.speedLabel}</td>
                            <td style={tdStyle}>{p.dataLimitLabel}</td>
                            <td style={tdStyle}>{p.ottCount}</td>
                            <td style={tdStyle}>{p.oltType}</td>
                            <td style={{ ...tdStyle, color: "#256D85", fontWeight: 500 }}>Rs. {p.monthlyPrice}</td>
                          </tr>
                        );
                      })}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {plans.find((p) => p.planId === selectedPlanId) && (
            <p style={{ fontSize: "13px", color: "#16a34a", margin: "0 0 16px 0", fontWeight: 500 }}>
              ✓ Selected: {plans.find((p) => p.planId === selectedPlanId)!.planName} — Rs.{plans.find((p) => p.planId === selectedPlanId)!.monthlyPrice}
            </p>
          )}

          <div style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", padding: "24px", maxWidth: "480px" }}>
            <form onSubmit={handleNewInstall} style={{ display: "flex", flexDirection: "column", gap: "14px" }}>
              <Field label="Customer Name">
                <input value={niForm.customerName} onChange={(e) => setNiForm({ ...niForm, customerName: e.target.value })}
                  required placeholder="Enter full name" style={inputStyle} onFocus={focusBorder} onBlur={blurBorder} />
              </Field>
              <Field label="Email">
                <input type="email" value={niForm.email} onChange={(e) => setNiForm({ ...niForm, email: e.target.value })}
                  required placeholder="Enter email address" style={inputStyle} onFocus={focusBorder} onBlur={blurBorder} />
              </Field>
              <Field label="Salary (Rs.)">
                <input type="number" value={niForm.salary} onChange={(e) => setNiForm({ ...niForm, salary: e.target.value })}
                  required placeholder="Minimum Rs. 30,000" style={inputStyle} onFocus={focusBorder} onBlur={blurBorder} />
              </Field>
              <Field label="Pincode">
                <>
                  <input
                    list="pincode-list"
                    value={niForm.pincode}
                    onChange={(e) => setNiForm({ ...niForm, pincode: e.target.value })}
                    required
                    placeholder="Type or select pincode"
                    style={inputStyle}
                    onFocus={focusBorder}
                    onBlur={blurBorder}
                  />
                  <datalist id="pincode-list">
                    {pincodes.map((pin) => <option key={pin} value={pin} />)}
                  </datalist>
                </>
              </Field>
              {niError   && <p style={errText}>{niError}</p>}
              {niSuccess && <p style={{ fontSize: "13px", color: "#16a34a", margin: 0, fontWeight: 500 }}>✓ {niSuccess}</p>}
              <div style={{ display: "flex", gap: "8px", justifyContent: "flex-end", marginTop: "4px" }}>
                <button type="button" onClick={resetNi} style={cancelBtn}>Clear</button>
                <button type="submit" disabled={niLoading} style={primaryBtn}>
                  {niLoading ? "Creating..." : "Create Connection"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ── CHANGE PLAN ── */}
      {view === "change" && (
        <div style={{ marginTop: "32px" }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "20px" }}>
            <h2 style={{ fontSize: "16px", fontWeight: 600, color: "#111827", margin: 0 }}>Change Plan</h2>
            <button onClick={goBack} style={cancelBtn}>← Back</button>
          </div>

          {/* Active Connections — search bar */}
          <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "8px" }}>
            <p style={{ ...sectionLabel, margin: 0 }}>ACTIVE CONNECTIONS — click a row to select</p>
            <div style={{ display: "flex", border: "1px solid #d1d5db", borderRadius: "3px", overflow: "hidden", background: "#ffffff" }}>
              <select
                value={cpConnSearchBy}
                onChange={(e) => { setCpConnSearchBy(e.target.value as "name" | "code"); setCpConnSearch(""); }}
                style={{ height: "30px", border: "none", borderRight: "1px solid #d1d5db", padding: "0 8px", fontSize: "12px", color: "#6b7280", background: "#f9fafb", outline: "none", cursor: "pointer" }}
              >
                <option value="name">Name</option>
                <option value="code">Code</option>
              </select>
              <input
                value={cpConnSearch}
                onChange={(e) => setCpConnSearch(e.target.value)}
                placeholder={cpConnSearchBy === "name" ? "Search by name..." : "Search by code..."}
                style={{ height: "30px", border: "none", padding: "0 10px", fontSize: "13px", outline: "none", width: "180px" }}
              />
            </div>
          </div>
          <div style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", overflow: "hidden", marginBottom: "24px" }}>
            {connsLoading ? (
              <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>Loading connections...</p>
            ) : connsError ? (
              <p style={{ padding: "16px", fontSize: "13px", color: "#dc2626" }}>{connsError}</p>
            ) : activeConns.length === 0 ? (
              <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>No active connections found.</p>
            ) : (
              <div style={{ overflowY: "auto", maxHeight: "308px" }}>
                <table style={{ width: "100%", borderCollapse: "collapse" }}>
                  <thead style={{ position: "sticky", top: 0, zIndex: 1 }}>
                    <tr style={{ background: "#f1f5f9" }}>
                      {["Conn ID", "Cust Code", "Customer", "Pincode", "Current Plan", "Price", "OLT", "Port"].map((h) => (
                        <th key={h} style={thStyle}>{h}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {activeConns
                      .filter((c) => {
                        const q = cpConnSearch.trim().toLowerCase();
                        if (!q) return true;
                        return cpConnSearchBy === "name"
                          ? c.fullName.toLowerCase().includes(q)
                          : c.customerCode.toLowerCase().includes(q);
                      })
                      .map((c, i) => {
                        const sel = selectedConn?.connectionId === c.connectionId;
                        return (
                          <tr key={c.connectionId} onClick={() => setSelectedConn(c)}
                            style={{ background: sel ? "#e0f2fe" : i % 2 === 1 ? "#fafafa" : "#ffffff", borderTop: "1px solid #e5e7eb", cursor: "pointer" }}>
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

          {/* Available Plans for selected connection */}
          {selectedConn && (
            <>
              <div style={{ background: "#f0fdf4", border: "1px solid #bbf7d0", borderRadius: "4px", padding: "12px 16px", marginBottom: "16px" }}>
                <p style={{ fontSize: "13px", color: "#166534", margin: 0 }}>
                  Selected: <strong>{selectedConn.fullName}</strong> ({selectedConn.customerCode}) — Current plan: <strong>{selectedConn.planName}</strong> @ Rs.{selectedConn.monthlyPrice}
                </p>
              </div>

              <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "8px" }}>
                <p style={{ ...sectionLabel, margin: 0 }}>AVAILABLE PLANS — click a row to select new plan</p>
                <input
                  value={cpPlanSearch}
                  onChange={(e) => setCpPlanSearch(e.target.value)}
                  placeholder="Search plan by name..."
                  style={{ height: "30px", border: "1px solid #d1d5db", borderRadius: "3px", padding: "0 10px", fontSize: "13px", outline: "none", width: "180px" }}
                />
              </div>
              <div style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", overflow: "hidden", marginBottom: "20px" }}>
                {availPlansLoading ? (
                  <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>Loading available plans...</p>
                ) : availPlans.length === 0 ? (
                  <p style={{ padding: "16px", fontSize: "13px", color: "#6b7280" }}>No other plans available for this service area.</p>
                ) : (
                  <div style={{ overflowY: "auto", maxHeight: "176px" }}>
                    <table style={{ width: "100%", borderCollapse: "collapse" }}>
                      <thead style={{ position: "sticky", top: 0, zIndex: 1 }}>
                        <tr style={{ background: "#f1f5f9" }}>
                          {["ID", "Plan Name", "Speed", "Data", "OTTs", "OLT Type", "Price / Month"].map((h) => (
                            <th key={h} style={thStyle}>{h}</th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        {availPlans
                          .filter((p) => p.planName.toLowerCase().includes(cpPlanSearch.toLowerCase()))
                          .map((p, i) => {
                          const sel = selectedNewPlanId === p.planId;
                          return (
                            <tr key={p.planId} onClick={() => setSelectedNewPlanId(p.planId)}
                              style={{ background: sel ? "#e0f2fe" : i % 2 === 1 ? "#fafafa" : "#ffffff", borderTop: "1px solid #e5e7eb", cursor: "pointer" }}>
                              <td style={tdStyle}>{p.planId}</td>
                              <td style={{ ...tdStyle, fontWeight: sel ? 600 : 400 }}>{p.planName}</td>
                              <td style={tdStyle}>{p.speedLabel}</td>
                              <td style={tdStyle}>{p.dataLimitLabel}</td>
                              <td style={tdStyle}>{p.ottCount}</td>
                              <td style={tdStyle}>{p.oltType}</td>
                              <td style={{ ...tdStyle, color: "#256D85", fontWeight: 500 }}>Rs. {p.monthlyPrice}</td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>

              {selectedNewPlan && (
                <div style={{ background: "#eff6ff", border: "1px solid #bfdbfe", borderRadius: "4px", padding: "12px 16px", marginBottom: "16px" }}>
                  <p style={{ fontSize: "13px", color: "#1e40af", margin: 0 }}>
                    Change from <strong>{selectedConn.planName}</strong> → <strong>{selectedNewPlan.planName}</strong> @ Rs.{selectedNewPlan.monthlyPrice}
                    {selectedConn.oltType !== selectedNewPlan.oltType && (
                      <span style={{ color: "#b45309", marginLeft: "8px" }}>⚠ OLT type will change, port will be reallocated.</span>
                    )}
                  </p>
                </div>
              )}

              {cpError   && <p style={{ ...errText, marginBottom: "12px" }}>{cpError}</p>}
              {cpSuccess && <p style={{ fontSize: "13px", color: "#16a34a", marginBottom: "12px" }}>{cpSuccess}</p>}

              <div style={{ display: "flex", gap: "8px" }}>
                <button onClick={() => { setSelectedConn(null); setAvailPlans([]); setSelectedNewPlanId(null); setCpError(""); setCpSuccess(""); }} style={cancelBtn}>
                  Cancel
                </button>
                <button
                  onClick={handleChangePlan}
                  disabled={!selectedNewPlanId || cpLoading}
                  style={{ ...primaryBtn, opacity: !selectedNewPlanId ? 0.5 : 1 }}
                >
                  {cpLoading ? "Changing..." : "Confirm Change"}
                </button>
              </div>
            </>
          )}
        </div>
      )}
      {/* ── DISCONNECT ── */}
      {view === "disconnect" && <Disconnect onBack={goBack} />}
    </div>
  );
}
