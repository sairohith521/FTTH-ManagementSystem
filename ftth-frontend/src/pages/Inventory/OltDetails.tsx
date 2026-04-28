import { useEffect, useState } from "react";
import { inventoryService } from "../../services/inventoryService";
import type { OltInventoryDTO, OltDetail, PortDetail } from "../../types/models";
import Card from "../../components/ui/Card";
import Select from "../../components/ui/Select";
import Input from "../../components/ui/Input";
import Table from "../../components/ui/Table";
import Badge from "../../components/ui/Badge";
import Loader from "../../components/ui/Loader";

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
    if (!pincode) { setOlts([]); setSelectedOlt(""); setDetails(null); setPorts([]); return; }
    if (!pincodes.includes(pincode)) { setOlts([]); setSelectedOlt(""); setDetails(null); setPorts([]); return; }
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

  // Group ports by splitter number
  const splitterMap = new Map<number, PortDetail[]>();
  for (const p of ports) {
    const list = splitterMap.get(p.splitterNumber) || [];
    list.push(p);
    splitterMap.set(p.splitterNumber, list);
  }
  const splitterNumbers = Array.from(splitterMap.keys()).sort((a, b) => a - b);

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
          <Input
            label="Pincode"
            value={pincode}
            onChange={(e) => { setPincode(e.target.value); setSelectedOlt(""); }}
            placeholder="Type or pick..."
            list="olt-details-pincode-list"
          />
          <datalist id="olt-details-pincode-list">
            {pincodes.map((p) => (
              <option key={p} value={p} />
            ))}
          </datalist>
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

          <h3>Splitter Summary</h3>
          <Table columns={splitterColumns} data={details.splitters} keyField="splitterNumber" />

          {splitterNumbers.length > 0 && (
            <>
              <div className="section-divider" />
              <h3>Port Allocation</h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {splitterNumbers.map((splNum) => {
                  const splPorts = splitterMap.get(splNum) || [];
                  return (
                    <div key={splNum} className="border border-border rounded-md overflow-hidden">
                      <div className="bg-sidebarBg text-white text-sm font-medium px-3 py-2">
                        Splitter {splNum}
                      </div>
                      <div className="divide-y divide-border">
                        {splPorts.map((port) => {
                          const assigned = port.portStatus === "ASSIGNED";
                          return (
                            <div
                              key={port.portNumber}
                              className={`flex items-center justify-between px-3 py-2 text-sm ${
                                assigned ? "bg-red-50" : "bg-green-50"
                              }`}
                            >
                              <span className="font-medium">Port {port.portNumber}</span>
                              <span className={`text-xs font-medium ${assigned ? "text-error" : "text-success"}`}>
                                {assigned ? port.customerCode || "ASSIGNED" : "AVAILABLE"}
                              </span>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  );
                })}
              </div>
            </>
          )}
        </div>
      )}
    </Card>
  );
}
