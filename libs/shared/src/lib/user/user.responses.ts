export interface RoleResponse {
  name: string;
}

export interface UserResponse {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email?: string;
  activated: boolean;
  nonLocked: boolean;
  roles?: RoleResponse[];
}
