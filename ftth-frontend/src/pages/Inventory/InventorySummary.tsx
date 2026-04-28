import { useEffect, useState } from "react";
import { inventoryService } from "../../services/inventoryService";
import type { OltInventoryDTO } from "../../types/models";
import Card from "../../components/ui/Card";
import Table from "../../components/ui/Table";
import Loader from "../../components/ui/Loader";
import Badge from "../../components/ui/Badge";

export default function InventorySummary() {
  const [data, setData] = useState<OltInventoryDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setError("");
    try {
      const pincodes = await inventoryService.getPincodes();
      const all: OltInventoryDTO[] = [];
      for (const pin of pincodes) {
        const olts = await inventoryService.getOltsByPincode(pin);
        all.push(...olts);
      }
      setData(all);
    } catch {
      setError("Failed to load inventory data.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loader />;
  if (error) return <p className="text-error">{error}</p>;

  const columns = [
    { key: "pincode", header: "Pincode" },
    { key: "oltCode", header: "OLT Code" },
    { key: "oltType", header: "Type", render: (r: OltInventoryDTO) => <Badge label={r.oltType} variant="neutral" /> },
    { key: "splitterCount", header: "Splitters" },
    {
      key: "ports",
      header: "Ports (Avail / Total)",
      render: (r: OltInventoryDTO) => {
        const pct = r.totalPorts > 0 ? (r.availablePorts / r.totalPorts) * 100 : 0;
        const variant = pct === 0 ? "error" : pct <= 30 ? "warning" : "success";
        return (
          <span>
            <Badge label={`${r.availablePorts} / ${r.totalPorts}`} variant={variant} />
          </span>
        );
      },
    },
  ];

  return (
    <Card>
      <h2 className="mb-3">Inventory Summary</h2>
      <Table columns={columns} data={data} keyField="oltCode" />
    </Card>
  );
}
