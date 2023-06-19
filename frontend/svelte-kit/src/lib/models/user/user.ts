import type { RoleDTO } from './role';

export interface UserDTO {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email?: string;
  active?: boolean;
  lock?: boolean;
  createdAt: Date;
  roles?: RoleDTO[];
}
