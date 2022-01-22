import { AuthState } from './auth/+state/auth.reducer';
import { UsersState } from './user/+state/users.reducer';

export interface AppState {
  auth: AuthState;
  users: UsersState;
}
