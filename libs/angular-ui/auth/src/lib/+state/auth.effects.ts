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
      switchMap((action) =>
        this.authService.login(action.request).pipe(
          map((response) => AuthActions.loginSuccess({ response })),
          catchError((error) => of(AuthActions.loginFailure(error)))
        )
      )
    )
  );

  refreshToken$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.refreshToken),
      switchMap((action) =>
        this.authService.refreshToken(action.request).pipe(
          map((response) => AuthActions.refreshTokenSuccess({ response })),
          catchError((error) => of(AuthActions.refreshTokenFailure(error)))
        )
      )
    )
  );

  signUp$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.signUp),
      switchMap((action) =>
        this.authService.signUp(action.request).pipe(
          map((response) => AuthActions.signUpSuccess({ response })),
          catchError((error) => of(AuthActions.signUpFailure(error)))
        )
      )
    )
  );

  resendConfirmationEmail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.resendConfirmationEmail),
      switchMap((action) =>
        this.authService.resendConfirmationEmail(action.request).pipe(
          map(() => AuthActions.resendConfirmationEmailSuccess()),
          catchError((error) => of(AuthActions.resendConfirmationEmailFailure(error)))
        )
      )
    )
  );

  forgotPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.forgotPassword),
      switchMap((action) =>
        this.authService.forgotPassword(action.request).pipe(
          map(() => AuthActions.forgotPasswordSuccess()),
          catchError((error) => of(AuthActions.forgotPasswordFailure(error)))
        )
      )
    )
  );

  resetPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.resetPassword),
      switchMap((action) =>
        this.authService.resetPassword(action.request).pipe(
          map(() => AuthActions.resetPasswordSuccess()),
          catchError((error) => of(AuthActions.resetPasswordFailure(error)))
        )
      )
    )
  );

  logout$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.logout),
      switchMap(() =>
        this.authService.logout().pipe(
          map(() => AuthActions.logoutSuccess()),
          catchError((error) => of(AuthActions.logoutFailure(error)))
        )
      )
    )
  );

  logoutFromAllDevices$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.logoutFromAllDevices),
      switchMap(() =>
        this.authService.logoutFromAllDevices().pipe(
          map(() => AuthActions.logoutFromAllDevicesSuccess()),
          catchError((error) => of(AuthActions.logoutFromAllDevicesFailure(error)))
        )
      )
    )
  );
}
