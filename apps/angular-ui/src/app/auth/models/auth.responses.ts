import { User } from '../../user/models/user.models';

export interface SignInResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}
export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}
