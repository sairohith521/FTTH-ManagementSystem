import { api } from "./apiClient";
import { ENDPOINTS } from "./endpoints";

interface LoginResponse {
  username: string;
  role: string;
  userId: string;
}

export const authService = {
  login: (username: string, password: string) =>
    api.post<LoginResponse>(ENDPOINTS.AUTH_LOGIN, { username, password }),
};
