import type { Role } from "../../types/roles";

export const sidebarItems: {
  label: string;
  path: string;
  roles: Role[];
}[] = [
  { label: "Dashboard", path: "/dashboard", roles: ["ADMIN", "CSR", "MAINT"] },
  { label: "Connections", path: "/connections", roles: ["ADMIN", "CSR"] },
  { label: "Customers", path: "/customers", roles: ["ADMIN", "CSR"] },
  { label: "Inventory", path: "/inventory", roles: ["ADMIN"] },
  { label: "Plan Admin", path: "/plans", roles: ["ADMIN"] },
  { label: "Capacity", path: "/capacity", roles: ["ADMIN", "MAINT"] },
  { label: "Users", path: "/users", roles: ["ADMIN"] },
  { label: "Maintenance", path: "/maintenance", roles: ["ADMIN", "MAINT"] },
];