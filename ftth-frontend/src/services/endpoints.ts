export const ENDPOINTS = {
  // Inventory
  INVENTORY_PINCODES: "/api/inventory/pincodes",
  INVENTORY_OLTS: "/api/inventory/olts",
  INVENTORY_OLT_DETAILS: (oltCode: string) => `/api/inventory/olts/${oltCode}/details`,
  INVENTORY_OLT_DELETE: (oltCode: string) => `/api/inventory/olts/${oltCode}`,
  INVENTORY_SPLITTER_ADD: (oltCode: string) => `/api/inventory/olts/${oltCode}/splitters`,
  INVENTORY_SPLITTER_DELETE: (oltCode: string, num: number) => `/api/inventory/olts/${oltCode}/splitters/${num}`,
  INVENTORY_CONFIG: "/api/inventory/config",

  // Auth
  AUTH_LOGIN: "/api/auth/login",

  // Plans
  PLANS: "/api/plans",
  PLAN_BY_ID: (id: number) => `/api/plans/${id}`,

  // Connections
  CONNECTIONS: "/api/connections",
  CONNECTION_BY_ID: (id: number) => `/api/connections/${id}`,

  // Customers
  CUSTOMERS: "/api/customers",
  CUSTOMER_BY_CODE: (code: string) => `/api/customers/${code}`,

  // Bills
  BILLS: "/api/bills",
  BILL_BY_ID: (id: number) => `/api/bills/${id}`,

  // Capacity
  CAPACITY: "/api/capacity",

  // Users
  USERS: "/api/users",
};
