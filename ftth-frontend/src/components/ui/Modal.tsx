import type { ReactNode } from "react";
import Button from "./Button";

interface Props {
  open: boolean;
  title: string;
  children: ReactNode;
  onConfirm: () => void;
  onCancel: () => void;
  confirmLabel?: string;
  danger?: boolean;
}

export default function Modal({ open, title, children, onConfirm, onCancel, confirmLabel = "Confirm", danger }: Props) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="bg-surface rounded-md border border-border p-6 w-full max-w-md shadow-lg">
        <h3 className="mb-3">{title}</h3>
        <div className="mb-5 text-sm text-textSecondary">{children}</div>
        <div className="flex justify-end gap-2">
          <Button variant="outline" onClick={onCancel}>Cancel</Button>
          <Button variant={danger ? "danger" : "primary"} onClick={onConfirm}>{confirmLabel}</Button>
        </div>
      </div>
    </div>
  );
}
