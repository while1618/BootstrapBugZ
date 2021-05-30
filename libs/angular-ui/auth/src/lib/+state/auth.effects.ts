import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { AuthService } from '../auth.service';
import * as AuthActions from './auth.actions';

@Injectable()
export class AuthEffects {
  constructor(private actions$: Actions, private authService: AuthService) {}

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.login),
      switchMap((data) =>
        this.authService.login(data.loginRequest).pipe(
          map((value) => AuthActions.loginSuccess({ token: 'dsadsa' })),
          catchError((error) => of(AuthActions.loginFailure({ error })))
        )
      )
    )
  );
}
