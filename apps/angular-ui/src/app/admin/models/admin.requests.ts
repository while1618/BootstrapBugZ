import { RoleName } from '../../user/models/user.models';

export interface AdminRequest {
  usernames: string[];
}

export interface UpdateRoleRequest {
  usernames: string[];
  roleNames: RoleName[];
}
