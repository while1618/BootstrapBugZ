import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { UserService } from '../services/user.service';
import * as UsersActions from './users.actions';

@Injectable()
export class UsersEffects {
  findAll$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UsersActions.findAll),
      switchMap(() =>
        this.userService.findAll().pipe(
          map((response) => UsersActions.findAllSuccess({ response })),
          catchError((error) => of(UsersActions.findAllFailure(error)))
        )
      )
    )
  );
  findByUsername$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UsersActions.findByUsername),
      switchMap((action) =>
        this.userService.findByUsername(action.request).pipe(
          map((response) => UsersActions.findByUsernameSuccess({ response })),
          catchError((error) => of(UsersActions.findByUsernameFailure(error)))
        )
      )
    )
  );

  constructor(private actions$: Actions, private userService: UserService) {}
}
