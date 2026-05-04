import { useEffect, useState } from "react";
import PageWrapper from "../../components/layout/PageWrapper";
import Card from "../../components/ui/Card";
import Table from "../../components/ui/Table";
import Badge from "../../components/ui/Badge";
import Loader from "../../components/ui/Loader";
import { api } from "../../services/apiClient";

interface CapacityOlt {
  pincode: string;
  oltCode: string;
  oltType: string;
  splitterCount: number;
  totalPorts: number;
  usedPorts: number;
  freePorts: number;
  utilPercent: number;
  breach: boolean;
  warning: "CAPACITY_BREACH" | "ADD_SPLITTER" | "ADD_OLT" | null;
}

interface CapacityResponse {
  threshold: number;
  totalOlts: number;
  breachCount: number;
  addSplitterCount: number;
  addOltCount: number;
  olts: CapacityOlt[];
}

export default function Capacity() {
  const [data, setData] = useState<CapacityResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<CapacityResponse>("/api/capacity")
      .then(setData)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loader />;
  if (!data) return null;

  const statusBadge = (o: CapacityOlt) => {
    if (o.warning === "ADD_OLT")
      return <Badge label="Add OLT" variant="error" />;

    if (o.warning === "ADD_SPLITTER")
      return <Badge label="Add Splitter" variant="warning" />;

    if (o.warning === "CAPACITY_BREACH")
      return <Badge label="Breach" variant="warning" />;

    return <Badge label="Normal" variant="success" />;
  };

  return (
    <PageWrapper title="Capacity Dashboard">
      {/* ---- Summary Cards ---- */}
      <div style={{ display: "flex", gap: "16px" }}>
        {[
          { label: "Threshold", value: `${data.threshold}%` },
          { label: "Total OLTs", value: data.totalOlts },
          { label: "Breaches", value: data.breachCount, color: "#f59e0b" },
        ].map((c) => (
          <div key={c.label} style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", padding: "24px 20px", flex: 1 }}>
            <div style={{ fontSize: "13px", color: "#6b7280" }}>{c.label}</div>
            <div style={{ fontSize: "20px", fontWeight: 600, color: c.color ?? "inherit" }}>{c.value}</div>
          </div>
        ))}
        <div style={{ background: "#ffffff", border: "1px solid #d1d5db", borderRadius: "4px", padding: "24px 20px", flex: 1 }}>
          <div style={{ fontSize: "13px", color: "#6b7280" }}>Expansion Needed</div>
          <div style={{ fontSize: "13px", marginTop: "4px" }}>Splitters: <b>{data.addSplitterCount}</b></div>
          <div style={{ fontSize: "13px" }}>OLTs: <b>{data.addOltCount}</b></div>
        </div>
      </div>

      {/* ---- Capacity Table ---- */}
      <Card>
        <Table
          keyField="oltCode"
          data={data.olts}
          columns={[
            { key: "pincode", header: "Pincode" },
            { key: "oltCode", header: "OLT Code" },
            { key: "oltType", header: "OLT Type" },
            { key: "splitterCount", header: "Splitters" },
            { key: "totalPorts", header: "Total" },
            { key: "usedPorts", header: "Used" },
            { key: "freePorts", header: "Free" },
            {
              key: "utilPercent",
              header: "Util %",
              render: (r) => (
                <span
                  className={
                    r.utilPercent >= data.threshold
                      ? "text-warning font-medium"
                      : ""
                  }
                >
                  {r.utilPercent}%
                </span>
              ),
            },
            {
              key: "status",
              header: "Status",
              render: statusBadge,
            },
          ]}
        />
      </Card>
    </PageWrapper>
  );
}
