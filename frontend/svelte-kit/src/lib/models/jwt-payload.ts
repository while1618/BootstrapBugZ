import type { RoleName } from './role';

export interface JwtPayload {
  iss: string;
  issuedAt: number;
  exp: number;
  purpose: JwtPurpose;
  roles?: RoleName[];
}

export enum JwtPurpose {
  ACCESS_TOKEN = 'ACCESS_TOKEN',
  REFRESH_TOKEN = 'REFRESH_TOKEN',
  CONFIRM_REGISTRATION_TOKEN = 'CONFIRM_REGISTRATION_TOKEN',
  RESET_PASSWORD_TOKEN = 'RESET_PASSWORD_TOKEN',
}
