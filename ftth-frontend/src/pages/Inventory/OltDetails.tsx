import { useEffect, useState } from "react";
import { inventoryService } from "../../services/inventoryService";
import type { OltInventoryDTO, OltDetail } from "../../types/models";
import Card from "../../components/ui/Card";
import Select from "../../components/ui/Select";
import Table from "../../components/ui/Table";
import Badge from "../../components/ui/Badge";
import Loader from "../../components/ui/Loader";

export default function OltDetails() {
  const [pincodes, setPincodes] = useState<string[]>([]);
  const [pincode, setPincode] = useState("");
  const [olts, setOlts] = useState<OltInventoryDTO[]>([]);
  const [selectedOlt, setSelectedOlt] = useState("");
  const [details, setDetails] = useState<OltDetail | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    inventoryService.getPincodes().then(setPincodes).catch(() => {});
  }, []);

  useEffect(() => {
    if (!pincode) { setOlts([]); setSelectedOlt(""); setDetails(null); return; }
    inventoryService.getOltsByPincode(pincode).then(setOlts).catch(() => {});
  }, [pincode]);

  useEffect(() => {
    if (!selectedOlt) { setDetails(null); return; }
    setLoading(true);
    inventoryService.getOltDetails(selectedOlt).then(setDetails).catch(() => {}).finally(() => setLoading(false));
  }, [selectedOlt]);

  const splitterColumns = [
    { key: "splitterNumber", header: "Splitter #" },
    { key: "splitterCode", header: "Code", render: (r: { splitterCode: string | null }) => r.splitterCode || "-" },
    {
      key: "ports",
      header: "Ports (Avail / Total)",
      render: (r: { availablePorts: number; totalPorts: number }) => {
        const variant = r.availablePorts === 0 ? "error" : r.availablePorts < r.totalPorts ? "warning" : "success";
        return <Badge label={`${r.availablePorts} / ${r.totalPorts}`} variant={variant} />;
      },
    },
  ];

  return (
    <Card>
      <h2 className="mb-4">OLT Details</h2>
      <div className="flex gap-4 mb-4 flex-wrap">
        <div className="w-48">
          <Select
            label="Pincode"
            value={pincode}
            onChange={(e) => { setPincode(e.target.value); setSelectedOlt(""); }}
            options={pincodes.map((p) => ({ value: p, label: p }))}
          />
        </div>
        {olts.length > 0 && (
          <div className="w-56">
            <Select
              label="OLT"
              value={selectedOlt}
              onChange={(e) => setSelectedOlt(e.target.value)}
              options={olts.map((o) => ({ value: o.oltCode, label: `${o.oltCode} (${o.oltType})` }))}
            />
          </div>
        )}
      </div>

      {loading && <Loader />}

      {!loading && details && (
        <div className="space-y-4">
          <div className="grid grid-cols-2 gap-x-8 gap-y-2 max-w-md text-sm">
            <span className="text-textSecondary">OLT Code</span>
            <span>{details.oltCode}</span>
            <span className="text-textSecondary">OLT Type</span>
            <span><Badge label={details.oltType} variant="neutral" /></span>
            <span className="text-textSecondary">Status</span>
            <span><Badge label={details.active ? "ACTIVE" : "INACTIVE"} variant={details.active ? "success" : "error"} /></span>
            <span className="text-textSecondary">Splitters</span>
            <span>{details.splitters.length}</span>
            <span className="text-textSecondary">Ports</span>
            <span>{details.availablePorts} / {details.totalPorts} available</span>
          </div>

          <div className="section-divider" />

          <h3>Splitter Breakdown</h3>
          <Table columns={splitterColumns} data={details.splitters} keyField="splitterNumber" />
        </div>
      )}
    </Card>
  );
}
