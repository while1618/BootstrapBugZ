import { User } from '@bootstrapbugz/shared';
import { Action, createReducer } from '@ngrx/store';

export const AUTH_FEATURE_KEY = 'auth';

export interface AuthState {
  isAuthenticated: boolean;
  token: string;
  user: User;
}

export const initialState: AuthState = {
  isAuthenticated: false,
  token: null,
  user: null,
};

const authReducer = createReducer(initialState);

export function reducer(state: AuthState | undefined, action: Action) {
  return authReducer(state, action);
}
