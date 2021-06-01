import { User } from '@bootstrapbugz/shared';
import { Action, createReducer, on } from '@ngrx/store';
import * as AuthActions from './auth.actions';

export const AUTH_FEATURE_KEY = 'auth';

export interface AuthState {
  isAuthenticated: boolean;
  token: string;
  user: User;
  error: Error;
}

export const initialState: AuthState = {
  isAuthenticated: false,
  token: null,
  user: null,
  error: null,
};

const authReducer = createReducer(
  initialState,
  on(AuthActions.login, (state, action) => ({ ...state })),
  on(AuthActions.loginSuccess, (state, action) => ({
    ...state,
    isAuthenticated: true,
    token: action.token,
  })),
  on(AuthActions.loginFailure, (state, action) => ({ ...state, error: action.error }))
);

export function reducer(state: AuthState | undefined, action: Action) {
  return authReducer(state, action);
}
