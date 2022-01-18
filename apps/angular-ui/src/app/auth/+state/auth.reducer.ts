import { Action, createReducer, on } from '@ngrx/store';
import { User } from '../../user/models/user.models';
import * as AuthActions from './auth.actions';

export const AUTH_FEATURE_KEY = 'auth';

export interface AuthState {
  isAuthenticated: boolean;
  user: User;
  error: Error;
  loading: boolean;
}

export const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
  error: null,
  loading: false,
};

const authReducer = createReducer(
  initialState,

  on(AuthActions.signUp, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.signUpSuccess, (state, action) => ({
    ...state,
    user: action.response,
    error: null,
    loading: false,
  })),
  on(AuthActions.signUpFailure, (state, action) => ({
    ...state,
    user: null,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.confirmRegistration, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.confirmRegistrationSuccess, (state, action) => ({
    ...state,
    error: null,
    loading: false,
  })),
  on(AuthActions.confirmRegistrationFailure, (state, action) => ({
    ...state,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.resendConfirmationEmail, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.resendConfirmationEmailSuccess, (state, action) => ({ ...state, loading: false })),
  on(AuthActions.resendConfirmationEmailFailure, (state, action) => ({
    ...state,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.signIn, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.signInSuccess, (state, action) => ({
    ...state,
    isAuthenticated: true,
    user: action.response.user,
    error: null,
    loading: false,
  })),
  on(AuthActions.signInFailure, (state, action) => ({
    ...state,
    isAuthenticated: false,
    user: null,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.refreshToken, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.refreshTokenSuccess, (state, action) => ({
    ...state,
    error: null,
    loading: false,
  })),
  on(AuthActions.refreshTokenFailure, (state, action) => ({
    ...state,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.signOut, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.signOutSuccess, (state, action) => ({
    ...state,
    isAuthenticated: false,
    user: null,
    loading: false,
  })),
  on(AuthActions.signOutFailure, (state, action) => ({
    ...state,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.signOutFromAllDevices, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.signOutFromAllDevicesSuccess, (state, action) => ({
    ...state,
    isAuthenticated: false,
    user: null,
    loading: false,
  })),
  on(AuthActions.signOutFromAllDevicesFailure, (state, action) => ({
    ...state,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.forgotPassword, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.forgotPasswordSuccess, (state, action) => ({ ...state, loading: false })),
  on(AuthActions.forgotPasswordFailure, (state, action) => ({
    ...state,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.resetPassword, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.resetPasswordSuccess, (state, action) => ({ ...state, loading: false })),
  on(AuthActions.resetPasswordFailure, (state, action) => ({
    ...state,
    error: action.error,
    loading: false,
  })),

  on(AuthActions.checkAuth, (state, action) => ({ ...state, loading: true })),
  on(AuthActions.checkAuthSuccess, (state, action) => ({
    ...state,
    isAuthenticated: true,
    user: action.response,
    error: null,
    loading: false,
  })),
  on(AuthActions.checkAuthFailure, (state, action) => ({
    ...state,
    isAuthenticated: false,
    user: null,
    error: action.error,
    loading: false,
  }))
);

export function reducer(state: AuthState | undefined, action: Action) {
  return authReducer(state, action);
}
