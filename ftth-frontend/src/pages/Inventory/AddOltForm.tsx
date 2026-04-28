import { useEffect, useState } from "react";
import { inventoryService } from "../../services/inventoryService";
import Card from "../../components/ui/Card";
import Input from "../../components/ui/Input";
import Select from "../../components/ui/Select";
import Button from "../../components/ui/Button";

interface Props {
  onSuccess: () => void;
}

export default function AddOltForm({ onSuccess }: Props) {
  const [pincode, setPincode] = useState("");
  const [oltType, setOltType] = useState("");
  const [splitterCount, setSplitterCount] = useState("1");
  const [maxSplitters, setMaxSplitters] = useState(3);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    inventoryService.getConfig().then((c) => setMaxSplitters(c.maxSplitters)).catch(() => {});
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMsg("");
    setError("");

    if (!pincode || !oltType || !splitterCount) {
      setError("All fields are required.");
      return;
    }

    const count = parseInt(splitterCount);
    if (count < 1 || count > maxSplitters) {
      setError(`Splitter count must be between 1 and ${maxSplitters}.`);
      return;
    }

    setLoading(true);
    try {
      const res = await inventoryService.addOlt({ pincode, oltType, splitterCount: count });
      setMsg(`OLT ${res.oltCode} added successfully.`);
      setPincode("");
      setOltType("");
      setSplitterCount("1");
      setTimeout(onSuccess, 1500);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to add OLT.");
    } finally {
      setLoading(false);
    }
  };

  const splitterOptions = Array.from({ length: maxSplitters }, (_, i) => ({
    value: String(i + 1),
    label: String(i + 1),
  }));

  return (
    <Card className="max-w-md">
      <h2 className="mb-4">Add OLT</h2>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <Input label="Pincode" value={pincode} onChange={(e) => setPincode(e.target.value)} placeholder="e.g. 562101" />
        <Select
          label="OLT Type"
          value={oltType}
          onChange={(e) => setOltType(e.target.value)}
          options={[
            { value: "OLT500", label: "OLT500 (1GBPS)" },
            { value: "OLT300", label: "OLT300 (400MBPS)" },
          ]}
        />
        <Select
          label="Splitter Count"
          value={splitterCount}
          onChange={(e) => setSplitterCount(e.target.value)}
          options={splitterOptions}
        />

        {error && <p className="text-error">{error}</p>}
        {msg && <p className="text-success">{msg}</p>}

        <Button type="submit" disabled={loading}>
          {loading ? "Adding..." : "Add OLT"}
        </Button>
      </form>
    </Card>
  );
}
