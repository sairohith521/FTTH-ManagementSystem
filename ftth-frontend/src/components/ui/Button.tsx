import type { ButtonHTMLAttributes } from "react";

type Variant = "primary" | "danger" | "outline";

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
}

const styles: Record<Variant, string> = {
  primary: "bg-primary text-white hover:bg-primaryHover",
  danger: "bg-danger text-white hover:bg-dangerHover",
  outline: "border border-border text-textPrimary hover:bg-background",
};

export default function Button({ variant = "primary", className = "", ...props }: Props) {
  return (
    <button
      className={`px-4 py-2 rounded-md text-sm font-medium cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed ${styles[variant]} ${className}`}
      {...props}
    />
  );
}
