import { User } from '@bootstrapbugz/shared';
import { Action, createReducer, on } from '@ngrx/store';
import * as AuthActions from './auth.actions';

export const AUTH_FEATURE_KEY = 'auth';

export interface AuthState {
  token: string;
  refreshToken: string;
  user: User;
  error: Error;
}

export const initialState: AuthState = {
  token: null,
  refreshToken: null,
  user: null,
  error: null,
};

const authReducer = createReducer(
  initialState,

  on(AuthActions.login, (state, action) => ({ ...state })),
  on(AuthActions.loginSuccess, (state, action) => ({
    ...state,
    user: action.loginResponse.user,
    token: action.loginResponse.token,
    refreshToken: action.loginResponse.refreshToken,
    error: null,
  })),
  on(AuthActions.loginFailure, (state, action) => ({
    ...state,
    user: null,
    token: null,
    refreshToken: null,
    error: action.error,
  })),

  on(AuthActions.refreshToken, (state, action) => ({ ...state })),
  on(AuthActions.refreshTokenSuccess, (state, action) => ({
    ...state,
    token: action.refreshTokenResponse.token,
    refreshToken: action.refreshTokenResponse.refreshToken,
    error: null,
  })),
  on(AuthActions.refreshTokenFailure, (state, action) => ({
    ...state,
    token: null,
    refreshToken: null,
    error: action.error,
  })),

  on(AuthActions.signUp, (state, action) => ({ ...state })),
  on(AuthActions.signUpSuccess, (state, action) => ({
    ...state,
    user: action.user,
    error: null,
  })),
  on(AuthActions.signUpFailure, (state, action) => ({ ...state, user: null, error: action.error })),

  on(AuthActions.resendConfirmationEmail, (state, action) => ({ ...state })),
  on(AuthActions.resendConfirmationEmailSuccess, (state, action) => ({ ...state })),
  on(AuthActions.resendConfirmationEmailFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),

  on(AuthActions.forgotPassword, (state, action) => ({ ...state })),
  on(AuthActions.forgotPasswordSuccess, (state, action) => ({ ...state })),
  on(AuthActions.forgotPasswordFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),

  on(AuthActions.resetPassword, (state, action) => ({ ...state })),
  on(AuthActions.resetPasswordSuccess, (state, action) => ({ ...state })),
  on(AuthActions.resetPasswordFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),

  on(AuthActions.logout, (state, action) => ({ ...state })),
  on(AuthActions.logoutSuccess, (state, action) => ({ ...state })),
  on(AuthActions.logoutFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),

  on(AuthActions.logoutFromAllDevices, (state, action) => ({ ...state })),
  on(AuthActions.logoutFromAllDevicesSuccess, (state, action) => ({ ...state })),
  on(AuthActions.logoutFromAllDevicesFailure, (state, action) => ({
    ...state,
    error: action.error,
  }))
);

export function reducer(state: AuthState | undefined, action: Action) {
  return authReducer(state, action);
}
