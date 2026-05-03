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
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <div className="text-sm text-textSecondary">Threshold</div>
          <div className="text-xl font-semibold">{data.threshold}%</div>
        </Card>

        <Card>
          <div className="text-sm text-textSecondary">Total OLTs</div>
          <div className="text-xl font-semibold">{data.totalOlts}</div>
        </Card>

        <Card>
          <div className="text-sm text-textSecondary">Breaches</div>
          <div className="text-xl font-semibold text-warning">
            {data.breachCount}
          </div>
        </Card>

        <Card>
          <div className="text-sm text-textSecondary">Expansion Needed</div>
          <div className="text-sm">
            Splitters: <b>{data.addSplitterCount}</b>
          </div>
          <div className="text-sm">
            OLTs: <b>{data.addOltCount}</b>
          </div>
        </Card>
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
