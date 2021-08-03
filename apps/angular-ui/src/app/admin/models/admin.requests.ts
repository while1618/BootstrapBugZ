import { RoleName } from '../../user/models/user.models';

export interface AdminRequest {
  usernames: string[];
}

export interface ChangeRoleRequest {
  usernames: string[];
  roleNames: RoleName[];
}
