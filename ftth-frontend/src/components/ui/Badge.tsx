type Variant = "success" | "error" | "warning" | "neutral";

interface Props {
  label: string;
  variant?: Variant;
}

const styles: Record<Variant, string> = {
  success: "bg-green-100 text-success",
  error: "bg-red-100 text-error",
  warning: "bg-amber-100 text-warning",
  neutral: "bg-gray-100 text-textSecondary",
};

export default function Badge({ label, variant = "neutral" }: Props) {
  return (
    <span className={`inline-block px-2 py-0.5 rounded text-xs font-medium ${styles[variant]}`}>
      {label}
    </span>
  );
}
