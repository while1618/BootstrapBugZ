import { RoleName } from '@bootstrapbugz/angular-ui/user/data';

export interface AdminRequest {
  usernames: string[];
}

export interface ChangeRoleRequest extends AdminRequest {
  roleNames: RoleName[];
}
