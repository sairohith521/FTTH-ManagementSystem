import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { api } from "../../services/apiClient";
import { ENDPOINTS } from "../../services/endpoints";
import type { Role } from "../../types/roles";

// ── Types ──
interface AdminStats {
  totalCustomers: number;
  activeConnections: number;
  availablePorts: number;
  totalRevenue: number;
  newConnectionsThisWeek: number;
  disconnectsThisWeek: number;
}

interface CapacityAlert {
  oltCode: string;
  usagePercent: number;
  totalPorts: number;
  usedPorts: number;
}

interface CsrStats {
  totalCustomers: number;
  newCustomersToday: number;
  pendingRequests: number;
  activeConnections: number;
}

interface RecentActivity {
  id: number;
  type: string;
  description: string;
  timestamp: string;
}

interface PlanInsight {
  planName: string;
  activeCount: number;
  monthlyPrice: number;
}

interface MaintStats {
  activeIssues: number;
  pendingTasks: number;
  resolvedToday: number;
  downConnections: number;
}

interface MaintIssue {
  id: number;
  area: string;
  description: string;
  severity: "HIGH" | "MEDIUM" | "LOW";
  status: string;
}

// ── Styles ──
const container: React.CSSProperties = {
  fontFamily: "'Source Sans 3', 'Segoe UI', sans-serif",
  padding: "24px",
  maxWidth: "1200px",
};

const sectionTitle: React.CSSProperties = {
  fontSize: "14px",
  fontWeight: 600,
  color: "#6b7280",
  textTransform: "uppercase",
  letterSpacing: "0.5px",
  margin: "28px 0 12px 0",
};

const statsGrid: React.CSSProperties = {
  display: "grid",
  gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))",
  gap: "14px",
};

const statCard: React.CSSProperties = {
  background: "#fff",
  border: "1px solid #e5e7eb",
  borderRadius: "8px",
  padding: "18px 16px",
};

const statValue: React.CSSProperties = {
  fontSize: "26px",
  fontWeight: 700,
  color: "#111827",
  margin: "0 0 2px 0",
};

const statLabel: React.CSSProperties = {
  fontSize: "13px",
  color: "#6b7280",
  margin: 0,
};

const actionGrid: React.CSSProperties = {
  display: "flex",
  gap: "12px",
  flexWrap: "wrap",
};

const actionBtn: React.CSSProperties = {
  background: "#2563eb",
  color: "#fff",
  border: "none",
  borderRadius: "6px",
  padding: "10px 18px",
  fontSize: "13px",
  fontWeight: 600,
  cursor: "pointer",
};

const alertCard: React.CSSProperties = {
  background: "#fef2f2",
  border: "1px solid #fecaca",
  borderRadius: "6px",
  padding: "12px 16px",
  marginBottom: "8px",
};

const insightCard: React.CSSProperties = {
  background: "#f9fafb",
  border: "1px solid #e5e7eb",
  borderRadius: "6px",
  padding: "12px 16px",
  marginBottom: "8px",
};

const badge = (color: string): React.CSSProperties => ({
  display: "inline-block",
  background: color,
  color: "#fff",
  fontSize: "11px",
  fontWeight: 600,
  borderRadius: "4px",
  padding: "2px 8px",
  marginLeft: "8px",
});

// ── Main Component ──
export default function Dashboard() {
  const { role, user } = useAuth();
  const navigate = useNavigate();

  const titleMap: Record<Role, string> = {
    ADMIN: "Administrator Dashboard",
    CSR: "Customer Service Dashboard",
    MAINT: "Maintenance Dashboard",
  };

  return (
    <div style={container}>
      <h1 style={{ fontSize: "22px", fontWeight: 600, color: "#111827", margin: "0 0 2px 0" }}>
        {titleMap[role]}
      </h1>
      <p style={{ fontSize: "14px", color: "#6b7280", margin: "0 0 8px 0" }}>
        Welcome back, {user}.
      </p>

      {role === "ADMIN" && <AdminDashboard navigate={navigate} />}
      {role === "CSR" && <CsrDashboard navigate={navigate} />}
      {role === "MAINT" && <MaintDashboard navigate={navigate} />}
    </div>
  );
}

// ══════════════════════════════════════════════
// ADMIN DASHBOARD
// ══════════════════════════════════════════════
function AdminDashboard({ navigate }: { navigate: ReturnType<typeof useNavigate> }) {
  const [stats, setStats] = useState<AdminStats | null>(null);
  const [alerts, setAlerts] = useState<CapacityAlert[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    api.get<AdminStats>(ENDPOINTS.DASHBOARD_ADMIN)
      .then(setStats)
      .catch(() => setStats({ totalCustomers: 0, activeConnections: 0, availablePorts: 0, totalRevenue: 0, newConnectionsThisWeek: 0, disconnectsThisWeek: 0 }));

    api.get<CapacityAlert[]>(ENDPOINTS.CAPACITY_SUMMARY)
      .then(setAlerts)
      .catch(() => setAlerts([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p style={{ color: "#6b7280" }}>Loading dashboard...</p>;

  const criticalAlerts = alerts.filter((a) => a.usagePercent >= 80);

  return (
    <>
      {/* Critical Alerts */}
      {criticalAlerts.length > 0 && (
        <>
          <p style={sectionTitle}>🔴 Critical Alerts</p>
          {criticalAlerts.map((a) => (
            <div key={a.oltCode} style={alertCard}>
              <span style={{ fontWeight: 600, color: "#991b1b" }}>{a.oltCode}</span>
              <span style={badge(a.usagePercent >= 95 ? "#dc2626" : "#f59e0b")}>
                {a.usagePercent}% full
              </span>
              <span style={{ fontSize: "13px", color: "#6b7280", marginLeft: "12px" }}>
                {a.usedPorts}/{a.totalPorts} ports used
              </span>
            </div>
          ))}
        </>
      )}

      {/* Stats */}
      <p style={sectionTitle}>📊 System Overview</p>
      {stats && (
        <div style={statsGrid}>
          <StatCard value={stats.totalCustomers} label="Total Customers" />
          <StatCard value={stats.activeConnections} label="Active Connections" />
          <StatCard value={stats.availablePorts} label="Available Ports" />
          <StatCard value={`₹${stats.totalRevenue.toLocaleString()}`} label="Monthly Revenue" />
        </div>
      )}

      {/* Capacity Insights */}
      {alerts.length > 0 && (
        <>
          <p style={sectionTitle}>🧠 Capacity Insights</p>
          {alerts.slice(0, 5).map((a) => (
            <div key={a.oltCode} style={insightCard}>
              <span style={{ fontWeight: 500 }}>{a.oltCode}</span>
              <span style={{ fontSize: "13px", color: "#6b7280", marginLeft: "12px" }}>
                {a.usagePercent}% utilized — {a.totalPorts - a.usedPorts} ports remaining
              </span>
              <ProgressBar percent={a.usagePercent} />
            </div>
          ))}
          <button style={{ ...actionBtn, background: "#4b5563", marginTop: "8px" }} onClick={() => navigate("/capacity")}>
            View Full Capacity →
          </button>
        </>
      )}

      {/* Trends */}
      {stats && (
        <>
          <p style={sectionTitle}>📈 This Week</p>
          <div style={statsGrid}>
            <StatCard value={stats.newConnectionsThisWeek} label="New Connections" color="#059669" />
            <StatCard value={stats.disconnectsThisWeek} label="Disconnects" color="#dc2626" />
          </div>
        </>
      )}

      {/* Quick Actions */}
      <p style={sectionTitle}>⚙️ Quick Actions</p>
      <div style={actionGrid}>
        <button style={actionBtn} onClick={() => navigate("/plans")}>Add Plan</button>
        <button style={actionBtn} onClick={() => navigate("/users")}>Add User</button>
        <button style={actionBtn} onClick={() => navigate("/inventory")}>Manage Inventory</button>
        <button style={actionBtn} onClick={() => navigate("/connections")}>New Connection</button>
        <button style={{ ...actionBtn, background: "#7c3aed" }} onClick={() => navigate("/maintenance")}>Maintenance</button>
      </div>
    </>
  );
}

// ══════════════════════════════════════════════
// CSR DASHBOARD
// ══════════════════════════════════════════════
function CsrDashboard({ navigate }: { navigate: ReturnType<typeof useNavigate> }) {
  const [stats, setStats] = useState<CsrStats | null>(null);
  const [recentActivity, setRecentActivity] = useState<RecentActivity[]>([]);
  const [topPlans, setTopPlans] = useState<PlanInsight[]>([]);
  const [searchCode, setSearchCode] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    api.get<CsrStats>(ENDPOINTS.DASHBOARD_CSR)
      .then((data) => {
        setStats(data);
        if ((data as any).recentActivity) setRecentActivity((data as any).recentActivity);
        if ((data as any).topPlans) setTopPlans((data as any).topPlans);
      })
      .catch(() => setStats({ totalCustomers: 0, newCustomersToday: 0, pendingRequests: 0, activeConnections: 0 }))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p style={{ color: "#6b7280" }}>Loading dashboard...</p>;

  return (
    <>
      {/* Stats */}
      <p style={sectionTitle}>👥 Customer Summary</p>
      {stats && (
        <div style={statsGrid}>
          <StatCard value={stats.totalCustomers} label="Total Customers" />
          <StatCard value={stats.newCustomersToday} label="New Today" color="#059669" />
          <StatCard value={stats.pendingRequests} label="Pending Requests" color="#f59e0b" />
          <StatCard value={stats.activeConnections} label="Active Connections" />
        </div>
      )}

      {/* Quick Actions */}
      <p style={sectionTitle}>📌 Quick Actions</p>
      <div style={actionGrid}>
        <button style={actionBtn} onClick={() => navigate("/customers")}>Add Customer</button>
        <button style={actionBtn} onClick={() => navigate("/connections")}>New Connection</button>
        <button style={{ ...actionBtn, background: "#7c3aed" }} onClick={() => navigate("/connections")}>Change Plan</button>
        <button style={{ ...actionBtn, background: "#dc2626" }} onClick={() => navigate("/connections")}>Disconnect</button>
      </div>

      {/* Quick Lookup */}
      <p style={sectionTitle}>🔍 Quick Customer Lookup</p>
      <div style={{ display: "flex", gap: "8px", alignItems: "center" }}>
        <input
          type="text"
          placeholder="Enter customer code..."
          value={searchCode}
          onChange={(e) => setSearchCode(e.target.value)}
          onKeyDown={(e) => { if (e.key === "Enter" && searchCode.trim()) navigate(`/customers?search=${searchCode.trim()}`); }}
          style={{ border: "1px solid #d1d5db", borderRadius: "6px", padding: "8px 12px", fontSize: "13px", width: "240px" }}
        />
        <button
          style={{ ...actionBtn, background: "#4b5563" }}
          onClick={() => { if (searchCode.trim()) navigate(`/customers?search=${searchCode.trim()}`); }}
        >
          Search
        </button>
      </div>

      {/* Top Plans */}
      {topPlans.length > 0 && (
        <>
          <p style={sectionTitle}>📦 Popular Plans</p>
          {topPlans.slice(0, 5).map((p) => (
            <div key={p.planName} style={insightCard}>
              <span style={{ fontWeight: 500 }}>{p.planName}</span>
              <span style={{ fontSize: "13px", color: "#6b7280", marginLeft: "12px" }}>
                {p.activeCount} active — ₹{p.monthlyPrice}/mo
              </span>
            </div>
          ))}
        </>
      )}

      {/* Recent Activity */}
      {recentActivity.length > 0 && (
        <>
          <p style={sectionTitle}>📋 Recent Activity</p>
          {recentActivity.slice(0, 5).map((a) => (
            <div key={a.id} style={insightCard}>
              <span style={{ fontWeight: 500, fontSize: "13px" }}>{a.type}</span>
              <span style={{ fontSize: "13px", color: "#6b7280", marginLeft: "8px" }}>{a.description}</span>
              <span style={{ fontSize: "11px", color: "#9ca3af", marginLeft: "auto", float: "right" }}>{a.timestamp}</span>
            </div>
          ))}
        </>
      )}
    </>
  );
}

// ══════════════════════════════════════════════
// MAINTENANCE DASHBOARD
// ══════════════════════════════════════════════
function MaintDashboard({ navigate }: { navigate: ReturnType<typeof useNavigate> }) {
  const [stats, setStats] = useState<MaintStats | null>(null);
  const [issues, setIssues] = useState<MaintIssue[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    api.get<MaintStats>(ENDPOINTS.DASHBOARD_MAINT)
      .then((data) => {
        setStats(data);
        if ((data as any).issues) setIssues((data as any).issues);
      })
      .catch(() => setStats({ activeIssues: 0, pendingTasks: 0, resolvedToday: 0, downConnections: 0 }))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p style={{ color: "#6b7280" }}>Loading dashboard...</p>;

  const severityColor = { HIGH: "#dc2626", MEDIUM: "#f59e0b", LOW: "#6b7280" };

  return (
    <>
      {/* Stats */}
      <p style={sectionTitle}>🚨 Overview</p>
      {stats && (
        <div style={statsGrid}>
          <StatCard value={stats.activeIssues} label="Active Issues" color="#dc2626" />
          <StatCard value={stats.downConnections} label="Down Connections" color="#dc2626" />
          <StatCard value={stats.pendingTasks} label="Pending Tasks" color="#f59e0b" />
          <StatCard value={stats.resolvedToday} label="Resolved Today" color="#059669" />
        </div>
      )}

      {/* Active Issues */}
      {issues.length > 0 && (
        <>
          <p style={sectionTitle}>📋 Active Issues</p>
          {issues.slice(0, 8).map((issue) => (
            <div key={issue.id} style={insightCard}>
              <span style={badge(severityColor[issue.severity])}>{issue.severity}</span>
              <span style={{ fontWeight: 500, marginLeft: "8px" }}>{issue.area}</span>
              <span style={{ fontSize: "13px", color: "#6b7280", marginLeft: "12px" }}>{issue.description}</span>
              <span style={{ fontSize: "12px", color: "#9ca3af", float: "right" }}>{issue.status}</span>
            </div>
          ))}
        </>
      )}

      {/* Quick Actions */}
      <p style={sectionTitle}>⚡ Quick Actions</p>
      <div style={actionGrid}>
        <button style={actionBtn} onClick={() => navigate("/maintenance")}>View All Tasks</button>
        <button style={{ ...actionBtn, background: "#059669" }} onClick={() => navigate("/maintenance")}>Mark Resolved</button>
        <button style={{ ...actionBtn, background: "#4b5563" }} onClick={() => navigate("/capacity")}>Check Capacity</button>
        <button style={{ ...actionBtn, background: "#7c3aed" }} onClick={() => navigate("/inventory")}>Inventory Status</button>
      </div>
    </>
  );
}

// ── Shared Components ──
function StatCard({ value, label, color }: { value: string | number; label: string; color?: string }) {
  return (
    <div style={statCard}>
      <p style={{ ...statValue, color: color || "#111827" }}>{value}</p>
      <p style={statLabel}>{label}</p>
    </div>
  );
}

function ProgressBar({ percent }: { percent: number }) {
  const barColor = percent >= 90 ? "#dc2626" : percent >= 70 ? "#f59e0b" : "#059669";
  return (
    <div style={{ background: "#e5e7eb", borderRadius: "4px", height: "6px", marginTop: "6px" }}>
      <div style={{ background: barColor, borderRadius: "4px", height: "6px", width: `${Math.min(percent, 100)}%` }} />
    </div>
  );
}
