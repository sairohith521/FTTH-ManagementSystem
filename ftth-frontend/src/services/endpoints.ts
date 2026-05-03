export const ENDPOINTS = {
  // Inventory
  INVENTORY_PINCODES: "/api/inventory/pincodes",
  INVENTORY_OLTS: "/api/inventory/olts",
  INVENTORY_OLT_DETAILS: (oltCode: string) => `/api/inventory/olts/${oltCode}/details`,
  INVENTORY_OLT_DELETE: (oltCode: string) => `/api/inventory/olts/${oltCode}`,
  INVENTORY_OLT_PORTS: (oltCode: string) => `/api/inventory/olts/${oltCode}/ports`,
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
  CONNECTION_NEW_INSTALL: "/api/connections/new-install",
  CONNECTION_ACTIVE: "/api/connections/active",
  CONNECTION_AVAILABLE_PLANS: (id: number) => `/api/connections/${id}/available-plans`,
  CONNECTION_CHANGE_PLAN: (id: number) => `/api/connections/${id}/change-plan`,
  CONNECTION_CHECK_MOVE: (id: number, pincode: string) => `/api/connections/${id}/check-move?pincode=${pincode}`,
  CONNECTION_MOVE: (id: number) => `/api/connections/${id}/move`,
  CONNECTION_DISCONNECT: (id: number) => `/api/connections/${id}/disconnect`,

  // Customers
  CUSTOMERS: "/api/customers",
  CUSTOMER_BY_CODE: (code: string) => `/api/customers/${code}`,
  CUSTOMER_CONNECTION: (code: string) => `/api/customers/${code}/connection`,
  CUSTOMER_BILLS: (code: string) => `/api/customers/${code}/bills`,
  CUSTOMER_GENERATE_BILL: (code: string) => `/api/customers/${code}/bills/generate`,
  BILL_PAY: (id: number) => `/api/customers/bills/${id}/pay`,
  BILL_OVERDUE: (id: number) => `/api/customers/bills/${id}/overdue`,

  // Bills
  BILLS: "/api/bills",
  BILL_BY_ID: (id: number) => `/api/bills/${id}`,

  // Capacity
  CAPACITY: "/api/capacity",

  // Users
  USERS: "/api/users",
  USER_BY_USERNAME: (username: string) => `/api/users/${username}`,
};
