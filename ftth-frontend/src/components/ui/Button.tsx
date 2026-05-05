import type { ButtonHTMLAttributes } from "react";

type Variant = "primary" | "danger" | "outline";

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
}

const styles: Record<Variant, string> = {
  primary: "btn-primary",
  danger: "btn-danger",
  outline: "btn-outline",
};

export default function Button({ variant = "primary", className = "", ...props }: Props) {
  return (
    <button className={`btn ${styles[variant]} ${className}`} {...props} />
  );
}
