import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AuthState, AUTH_FEATURE_KEY } from './auth.reducer';

export const getAuthState = createFeatureSelector<AuthState>(AUTH_FEATURE_KEY);

export const getIsAuthenticated = createSelector(
  getAuthState,
  (state: AuthState) => state.isAuthenticated
);

export const getSignedInUser = createSelector(
  getAuthState,
  (state: AuthState) => state.signedInUser
);

export const getError = createSelector(getAuthState, (state: AuthState) => state.error);

export const getLoading = createSelector(getAuthState, (state: AuthState) => state.loading);