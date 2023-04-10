import type { UserDTO } from '../user/user';

export interface SignInDTO {
  accessToken: string;
  refreshToken: string;
  user: UserDTO;
}
