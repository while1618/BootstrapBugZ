export enum RoleName {
  USER,
  ADMIN,
}

export interface Role {
  name: RoleName;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  activated: boolean;
  nonLocked: boolean;
  roles: Role[];
}
