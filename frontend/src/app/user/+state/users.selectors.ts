import { createFeatureSelector, createSelector } from '@ngrx/store';
import { UsersState, USERS_FEATURE_KEY } from './users.reducer';

export const getUsersState = createFeatureSelector<UsersState>(USERS_FEATURE_KEY);

export const getUsers = createSelector(getUsersState, (state: UsersState) => state.users);

export const getError = createSelector(getUsersState, (state: UsersState) => state.error);

export const getLoading = createSelector(getUsersState, (state: UsersState) => state.loading);
