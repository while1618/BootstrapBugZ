import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AUTH_FEATURE_KEY, AuthState } from './auth.reducer';

export const getAuthState = createFeatureSelector(AUTH_FEATURE_KEY);

export const getIsAuthenticated = createSelector(
  getAuthState,
  (state: AuthState) => state.isAuthenticated
);

export const getUser = createSelector(getAuthState, (state: AuthState) => state.user);

export const getError = createSelector(getAuthState, (state: AuthState) => state.error);

export const getLoading = createSelector(getAuthState, (state: AuthState) => state.loading);
