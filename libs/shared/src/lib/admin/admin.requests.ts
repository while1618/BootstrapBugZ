import { RoleName } from '../user/user.models';

export interface AdminRequest {
  usernames: string[];
}

export interface ChangeRoleRequest extends AdminRequest {
  roleNames: RoleName[];
}
