import { Action, createReducer, on } from '@ngrx/store';
import { User } from '../models/user.models';
import * as UsersActions from './users.actions';

export const USERS_FEATURE_KEY = 'users';

export interface UsersState {
  users: User[];
  error: Error;
  loading: boolean;
}

export const initialState: UsersState = {
  users: [],
  error: null,
  loading: false,
};

const usersReducer = createReducer(
  initialState,

  on(UsersActions.findAll, (state, action) => ({
    ...state,
    users: [],
    loading: true,
    error: null,
  })),
  on(UsersActions.findAllSuccess, (state, action) => ({
    ...state,
    users: action.response,
    loading: false,
    error: null,
  })),
  on(UsersActions.findAllFailure, (state, action) => ({
    ...state,
    users: [],
    loading: false,
    error: action.error,
  })),

  on(UsersActions.findByUsername, (state, action) => ({
    ...state,
    users: [],
    loading: false,
    error: null,
  })),
  on(UsersActions.findByUsernameSuccess, (state, action) => ({
    ...state,
    users: [action.response],
    loading: false,
    error: null,
  })),
  on(UsersActions.findByUsernameFailure, (state, action) => ({
    ...state,
    users: [],
    loading: false,
    error: action.error,
  }))
);

export function reducer(state: UsersState | undefined, action: Action) {
  return usersReducer(state, action);
}
