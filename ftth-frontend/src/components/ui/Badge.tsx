type Variant = "success" | "error" | "warning" | "neutral";

interface Props {
  label: string;
  variant?: Variant;
}

const styles: Record<Variant, string> = {
  success: "badge-success",
  error:   "badge-error",
  warning: "badge-warning",
  neutral: "badge-neutral",
};

export default function Badge({ label, variant = "neutral" }: Props) {
  return <span className={`badge ${styles[variant]}`}>{label}</span>;
}
