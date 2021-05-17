export interface SimpleUser {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}

export enum RoleName {
  USER,
  ADMIN,
}

export interface Role {
  name: RoleName;
}

export interface User extends SimpleUser {
  id: number;
  updatedAt: Date;
  lastLogout: Date;
  activated: boolean;
  nonLocked: boolean;
  roles: Role[];
}
