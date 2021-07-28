import { RoleName } from '@bootstrapbugz/angular-ui/user';

export interface AdminRequest {
  usernames: string[];
}

export interface ChangeRoleRequest {
  usernames: string[];
  roleNames: RoleName[];
}
