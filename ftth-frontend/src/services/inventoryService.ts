import { api } from "./apiClient";
import { ENDPOINTS } from "./endpoints";
import type {
  OltInventoryDTO,
  OltDetail,
  InventoryConfig,
  AddOltRequest,
  AddOltResponse,
  ApiMessage,
} from "../types/models";

export const inventoryService = {
  getPincodes: () =>
    api.get<string[]>(ENDPOINTS.INVENTORY_PINCODES),

  getOltsByPincode: (pincode: string) =>
    api.get<OltInventoryDTO[]>(`${ENDPOINTS.INVENTORY_OLTS}?pincode=${pincode}`),

  getOltDetails: (oltCode: string) =>
    api.get<OltDetail>(ENDPOINTS.INVENTORY_OLT_DETAILS(oltCode)),

  getConfig: () =>
    api.get<InventoryConfig>(ENDPOINTS.INVENTORY_CONFIG),

  addOlt: (req: AddOltRequest) =>
    api.post<AddOltResponse>(ENDPOINTS.INVENTORY_OLTS, req),

  removeOlt: (oltCode: string) =>
    api.del<ApiMessage>(ENDPOINTS.INVENTORY_OLT_DELETE(oltCode)),

  addSplitter: (oltCode: string) =>
    api.post<ApiMessage>(ENDPOINTS.INVENTORY_SPLITTER_ADD(oltCode), {}),

  removeSplitter: (oltCode: string, splitterNumber: number) =>
    api.del<ApiMessage>(ENDPOINTS.INVENTORY_SPLITTER_DELETE(oltCode, splitterNumber)),
};
