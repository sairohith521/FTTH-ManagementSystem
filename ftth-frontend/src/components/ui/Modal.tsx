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
    <div className="modal-overlay">
      <div className="modal-box">
        <div className="modal-title">{title}</div>
        <div className="modal-body">{children}</div>
        <div className="modal-actions">
          <Button variant="outline" onClick={onCancel}>Cancel</Button>
          <Button variant={danger ? "danger" : "primary"} onClick={onConfirm}>{confirmLabel}</Button>
        </div>
      </div>
    </div>
  );
}
