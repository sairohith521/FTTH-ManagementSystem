import React from "react";

export function Overlay({ children }: { children: React.ReactNode }) {
  return (
    <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.35)", zIndex: 50, display: "flex", alignItems: "center", justifyContent: "center" }}>
      {children}
    </div>
  );
}

export function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
      <label style={{ fontSize: "13px", color: "#6b7280" }}>{label}</label>
      {children}
    </div>
  );
}

export const focusBorder = (e: React.FocusEvent<HTMLInputElement | HTMLSelectElement>) => {
  e.target.style.borderColor = "#256D85";
};
export const blurBorder = (e: React.FocusEvent<HTMLInputElement | HTMLSelectElement>) => {
  e.target.style.borderColor = "#d1d5db";
};

export const inputStyle: React.CSSProperties = {
  height: "36px",
  border: "1px solid #d1d5db",
  borderRadius: "3px",
  padding: "0 10px",
  fontSize: "13px",
  color: "#111827",
  background: "#ffffff",
  outline: "none",
  boxShadow: "none",
};

export const primaryBtn: React.CSSProperties = {
  height: "36px",
  padding: "0 16px",
  background: "#256D85",
  color: "#ffffff",
  border: "none",
  borderRadius: "3px",
  fontSize: "14px",
  fontWeight: 600,
  cursor: "pointer",
};

export const cancelBtn: React.CSSProperties = {
  height: "36px",
  padding: "0 16px",
  background: "#ffffff",
  color: "#111827",
  border: "1px solid #d1d5db",
  borderRadius: "3px",
  fontSize: "14px",
  cursor: "pointer",
};

export const outlineBtn: React.CSSProperties = {
  height: "30px",
  padding: "0 12px",
  background: "#ffffff",
  color: "#256D85",
  border: "1px solid #256D85",
  borderRadius: "3px",
  fontSize: "13px",
  cursor: "pointer",
};

export const thStyle: React.CSSProperties = {
  padding: "10px 14px",
  fontSize: "13px",
  fontWeight: 600,
  color: "#6b7280",
  textAlign: "left",
};

export const tdStyle: React.CSSProperties = {
  padding: "10px 14px",
  fontSize: "14px",
  color: "#111827",
};

export const modalBox: React.CSSProperties = {
  background: "#ffffff",
  border: "1px solid #e5e7eb",
  borderRadius: "4px",
  padding: "24px",
  width: "100%",
  maxWidth: "400px",
};

export const modalTitle: React.CSSProperties = {
  fontSize: "16px",
  fontWeight: 600,
  color: "#111827",
  margin: "0 0 16px 0",
};

export const errText: React.CSSProperties = {
  fontSize: "13px",
  color: "#dc2626",
  margin: 0,
};
