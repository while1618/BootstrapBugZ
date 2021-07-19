import { UserResponse } from '../user/user.responses';

export interface RefreshTokenResponse {
  token: string;
  refreshToken: string;
}

export interface LoginResponse {
  user: UserResponse;
  token: string;
  refreshToken: string;
}
