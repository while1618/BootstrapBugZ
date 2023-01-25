import type { RoleDTO } from './role';

export interface UserDTO {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  activated: boolean;
  nonLocked: boolean;
  roles: RoleDTO[];
}
