import type { Role } from './role';

export interface AdminUser {
  id: number;
  username: string;
  email: string;
  active: boolean;
  lock: boolean;
  createdAt: Date;
  roles: Role[];
}

export interface SimplifiedUser {
  id: number;
  username: string;
  createdAt: Date;
}

export interface Profile {
  id: number;
  username: string;
  email: string;
  createdAt: Date;
}
