import { User } from '../../user/models/user.models';

export interface RefreshTokenResponse {
  token: string;
  refreshToken: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: User;
}
