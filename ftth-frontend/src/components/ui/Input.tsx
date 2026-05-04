import type { InputHTMLAttributes } from "react";

interface Props extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

export default function Input({ label, className = "", ...props }: Props) {
  return (
    <div className="form-field">
      {label && <label className="form-label">{label}</label>}
      <input className={`form-input ${className}`} {...props} />
    </div>
  );
}
