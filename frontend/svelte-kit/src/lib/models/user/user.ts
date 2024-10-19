import type { Role } from './role';

export interface User {
  id: number;
  username: string;
  email?: string;
  active?: boolean;
  lock?: boolean;
  createdAt: Date;
  roles?: Role[];
}
