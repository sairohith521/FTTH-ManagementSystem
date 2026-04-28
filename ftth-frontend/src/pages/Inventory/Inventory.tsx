import { useState } from "react";
import PageWrapper from "../../components/layout/PageWrapper";
import Button from "../../components/ui/Button";
import InventorySummary from "./InventorySummary";
import AddOltForm from "./AddOltForm";
import RemoveOltForm from "./RemoveOltForm";
import AddSplitterForm from "./AddSplitterForm";
import RemoveSplitterForm from "./RemoveSplitterForm";
import OltDetails from "./OltDetails";

const tabs = [
  { key: "summary", label: "Summary" },
  { key: "addOlt", label: "Add OLT" },
  { key: "removeOlt", label: "Remove OLT" },
  { key: "addSplitter", label: "Add Splitter" },
  { key: "removeSplitter", label: "Remove Splitter" },
  { key: "details", label: "OLT Details" },
] as const;

type Tab = (typeof tabs)[number]["key"];

export default function Inventory() {
  const [activeTab, setActiveTab] = useState<Tab>("summary");

  return (
    <PageWrapper title="Inventory Admin">
      <div className="flex gap-2 flex-wrap">
        {tabs.map((t) => (
          <Button
            key={t.key}
            variant={activeTab === t.key ? "primary" : "outline"}
            onClick={() => setActiveTab(t.key)}
            className="text-xs"
          >
            {t.label}
          </Button>
        ))}
      </div>

      <div className="mt-2">
        {activeTab === "summary" && <InventorySummary />}
        {activeTab === "addOlt" && <AddOltForm onSuccess={() => setActiveTab("summary")} />}
        {activeTab === "removeOlt" && <RemoveOltForm onSuccess={() => setActiveTab("summary")} />}
        {activeTab === "addSplitter" && <AddSplitterForm onSuccess={() => setActiveTab("summary")} />}
        {activeTab === "removeSplitter" && <RemoveSplitterForm onSuccess={() => setActiveTab("summary")} />}
        {activeTab === "details" && <OltDetails />}
      </div>
    </PageWrapper>
  );
}
