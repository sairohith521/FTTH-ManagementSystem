import { useEffect, useState } from "react";
import { inventoryService } from "../../services/inventoryService";
import type { OltInventoryDTO } from "../../types/models";
import Card from "../../components/ui/Card";
import Table from "../../components/ui/Table";
import Loader from "../../components/ui/Loader";
import Badge from "../../components/ui/Badge";
import Select from "../../components/ui/Select";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";

type SearchBy = "" | "pincode" | "oltType";

export default function InventorySummary() {
  const [allData, setAllData] = useState<OltInventoryDTO[]>([]);
  const [filtered, setFiltered] = useState<OltInventoryDTO[]>([]);
  const [pincodes, setPincodes] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [searchBy, setSearchBy] = useState<SearchBy>("");
  const [pincodeValue, setPincodeValue] = useState("");
  const [oltTypeValue, setOltTypeValue] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setError("");
    try {
      const pins = await inventoryService.getPincodes();
      setPincodes(pins);
      const all: OltInventoryDTO[] = [];
      for (const pin of pins) {
        const olts = await inventoryService.getOltsByPincode(pin);
        all.push(...olts);
      }
      setAllData(all);
      setFiltered(all);
    } catch {
      setError("Failed to load inventory data.");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    if (searchBy === "pincode" && pincodeValue.trim()) {
      setFiltered(allData.filter((r) => r.pincode === pincodeValue.trim()));
    } else if (searchBy === "oltType" && oltTypeValue) {
      setFiltered(allData.filter((r) => r.oltType === oltTypeValue));
    } else {
      setFiltered(allData);
    }
  };

  const handleReset = () => {
    setSearchBy("");
    setPincodeValue("");
    setOltTypeValue("");
    setFiltered(allData);
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
        return <Badge label={`${r.availablePorts} / ${r.totalPorts}`} variant={variant} />;
      },
    },
  ];

  return (
    <Card>
      <h2 className="mb-3">Inventory Summary</h2>

      <div className="flex items-end gap-3 flex-wrap mb-4">
        <div className="w-40">
          <Select
            label="Search By"
            value={searchBy}
            onChange={(e) => {
              setSearchBy(e.target.value as SearchBy);
              setPincodeValue("");
              setOltTypeValue("");
            }}
            options={[
              { value: "pincode", label: "Pincode" },
              { value: "oltType", label: "OLT Type" },
            ]}
          />
        </div>

        {searchBy === "pincode" && (
          <div className="w-48">
            <Input
              label="Pincode"
              value={pincodeValue}
              onChange={(e) => setPincodeValue(e.target.value)}
              placeholder="Type or pick..."
              list="pincode-list"
            />
            <datalist id="pincode-list">
              {pincodes.map((p) => (
                <option key={p} value={p} />
              ))}
            </datalist>
          </div>
        )}

        {searchBy === "oltType" && (
          <div className="w-48">
            <Select
              label="OLT Type"
              value={oltTypeValue}
              onChange={(e) => setOltTypeValue(e.target.value)}
              options={[
                { value: "OLT300", label: "OLT300" },
                { value: "OLT500", label: "OLT500" },
              ]}
            />
          </div>
        )}

        {searchBy && (
          <>
            <Button onClick={handleSearch} className="text-xs">Search</Button>
            <Button variant="outline" onClick={handleReset} className="text-xs">Reset</Button>
          </>
        )}
      </div>

      <Table columns={columns} data={filtered} keyField="oltCode" />
    </Card>
  );
}
