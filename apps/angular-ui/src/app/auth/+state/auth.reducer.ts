import { Action, createReducer, on } from '@ngrx/store';
import { User } from '../../user/models/user.models';
import * as AuthActions from './auth.actions';

export const AUTH_FEATURE_KEY = 'auth';

export interface AuthState {
  isAuthenticated: boolean;
  user: User;
  error: Error;
}

export const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
  error: null,
};

const authReducer = createReducer(
  initialState,
  // LOGIN
  on(AuthActions.login, (state, action) => ({ ...state })),
  on(AuthActions.loginSuccess, (state, action) => ({
    ...state,
    isAuthenticated: true,
    user: action.response.user,
    error: null,
  })),
  on(AuthActions.loginFailure, (state, action) => ({
    ...state,
    isAuthenticated: false,
    user: null,
    error: action.error,
  })),
  // REFRESH TOKEN
  on(AuthActions.refreshToken, (state, action) => ({ ...state })),
  on(AuthActions.refreshTokenSuccess, (state, action) => ({
    ...state,
    error: null,
  })),
  on(AuthActions.refreshTokenFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),
  // SIGN UP
  on(AuthActions.signUp, (state, action) => ({ ...state })),
  on(AuthActions.signUpSuccess, (state, action) => ({
    ...state,
    user: action.response,
    error: null,
  })),
  on(AuthActions.signUpFailure, (state, action) => ({ ...state, user: null, error: action.error })),
  // RESEND CONFIRMATION EMAIL
  on(AuthActions.resendConfirmationEmail, (state, action) => ({ ...state })),
  on(AuthActions.resendConfirmationEmailSuccess, (state, action) => ({ ...state })),
  on(AuthActions.resendConfirmationEmailFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),
  // FORGOT PASSWORD
  on(AuthActions.forgotPassword, (state, action) => ({ ...state })),
  on(AuthActions.forgotPasswordSuccess, (state, action) => ({ ...state })),
  on(AuthActions.forgotPasswordFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),
  // RESET PASSWORD
  on(AuthActions.resetPassword, (state, action) => ({ ...state })),
  on(AuthActions.resetPasswordSuccess, (state, action) => ({ ...state })),
  on(AuthActions.resetPasswordFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),
  // LOGOUT
  on(AuthActions.logout, (state, action) => ({ ...state })),
  on(AuthActions.logoutSuccess, (state, action) => ({ ...state })),
  on(AuthActions.logoutFailure, (state, action) => ({
    ...state,
    error: action.error,
  })),
  // LOGOUT FROM ALL DEVICES
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
