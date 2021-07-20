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
          map((response) => AuthActions.loginSuccess({ loginResponse: response })),
          catchError((error) => of(AuthActions.loginFailure({ error })))
        )
      )
    )
  );

  refreshToken$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.refreshToken),
      switchMap((data) =>
        this.authService.refreshToken(data.refreshTokenRequest).pipe(
          map((response) => AuthActions.refreshTokenSuccess({ refreshTokenResponse: response })),
          catchError((error) => of(AuthActions.refreshTokenFailure({ error })))
        )
      )
    )
  );

  signUp$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.signUp),
      switchMap((data) =>
        this.authService.signUp(data.signUpRequest).pipe(
          map((response) => AuthActions.signUpSuccess({ user: response })),
          catchError((error) => of(AuthActions.signUpFailure({ error })))
        )
      )
    )
  );

  resendConfirmationEmail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.resendConfirmationEmail),
      switchMap((data) =>
        this.authService.resendConfirmationEmail(data.resendConfirmationEmailRequest).pipe(
          map(() => AuthActions.resendConfirmationEmailSuccess()),
          catchError((error) => of(AuthActions.resendConfirmationEmailFailure({ error })))
        )
      )
    )
  );

  forgotPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.forgotPassword),
      switchMap((data) =>
        this.authService.forgotPassword(data.forgotPasswordRequest).pipe(
          map(() => AuthActions.forgotPasswordSuccess()),
          catchError((error) => of(AuthActions.forgotPasswordFailure({ error })))
        )
      )
    )
  );

  resetPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.resetPassword),
      switchMap((data) =>
        this.authService.resetPassword(data.resetPasswordRequest).pipe(
          map(() => AuthActions.resetPasswordSuccess()),
          catchError((error) => of(AuthActions.resetPasswordFailure({ error })))
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
          catchError((error) => of(AuthActions.logoutFailure({ error })))
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
          catchError((error) => of(AuthActions.logoutFromAllDevicesFailure({ error })))
        )
      )
    )
  );
}
