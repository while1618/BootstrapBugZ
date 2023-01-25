import type { UserDTO } from './user';

export interface SignInDTO {
  accessToken: string;
  refreshToken: string;
  user: UserDTO;
}
