import { AuthState } from './auth/+state/auth.reducer';

export interface AppState {
  auth: AuthState;
}
