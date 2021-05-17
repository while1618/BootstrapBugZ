export interface SimpleUser {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}

export interface Role {
  name: string;
}

export interface User extends SimpleUser {
  id: number;
  updatedAt: Date;
  lastLogout: Date;
  activated: boolean;
  nonLocked: boolean;
  roles: Role[];
}
