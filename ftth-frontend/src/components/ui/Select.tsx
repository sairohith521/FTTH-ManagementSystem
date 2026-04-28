import type { SelectHTMLAttributes } from "react";

interface Props extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  options: { value: string; label: string }[];
}

export default function Select({ label, options, className = "", ...props }: Props) {
  return (
    <div className="flex flex-col gap-1">
      {label && <label className="text-[13px] font-medium text-textSecondary">{label}</label>}
      <select
        className={`border border-border rounded-md px-3 py-2 text-sm bg-surface text-textPrimary outline-none focus:border-primary ${className}`}
        {...props}
      >
        <option value="">Select...</option>
        {options.map((o) => (
          <option key={o.value} value={o.value}>{o.label}</option>
        ))}
      </select>
    </div>
  );
}
