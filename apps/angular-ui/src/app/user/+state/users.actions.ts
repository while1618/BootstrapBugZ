import { createAction, props } from '@ngrx/store';
import { User } from '../models/user.models';

export const findAll = createAction('[Users] Find All');
export const findAllSuccess = createAction(
  '[Users] Find All Success',
  props<{ response: User[] }>()
);
export const findAllFailure = createAction('[Users] Find All Failure', props<{ error: Error }>());

export const findByUsername = createAction(
  '[Users] Find By Username',
  props<{ request: string }>()
);
export const findByUsernameSuccess = createAction(
  '[Users] Find By Username Success',
  props<{ response: User }>()
);
export const findByUsernameFailure = createAction(
  '[Users] Find By Username Failure',
  props<{ error: Error }>()
);
