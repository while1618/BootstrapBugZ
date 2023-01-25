import type { Role } from './role';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  activated: boolean;
  nonLocked: boolean;
  roles: Role;
}
