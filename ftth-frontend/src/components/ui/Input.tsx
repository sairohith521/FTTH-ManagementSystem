import type { InputHTMLAttributes } from "react";

interface Props extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

export default function Input({ label, className = "", ...props }: Props) {
  return (
    <div className="flex flex-col gap-1">
      {label && <label className="text-[13px] font-medium text-textSecondary">{label}</label>}
      <input
        className={`border border-border rounded-md px-3 py-2 text-sm bg-surface text-textPrimary outline-none focus:border-primary ${className}`}
        {...props}
      />
    </div>
  );
}
