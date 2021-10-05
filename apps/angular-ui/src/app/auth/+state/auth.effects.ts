import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import * as AuthActions from './auth.actions';

@Injectable()
export class AuthEffects {
  constructor(
    private actions$: Actions,
    private authService: AuthService,
    private router: Router
  ) {}

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.login),
      switchMap((action) =>
        this.authService.login(action.request).pipe(
          map((response) => AuthActions.loginSuccess({ response })),
          tap(() => this.router.navigate(['/home'])),
          catchError((error) => of(AuthActions.loginFailure(error)))
        )
      )
    )
  );
  checkAuth$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.checkAuth),
      switchMap((action) =>
        this.authService.getLoggedInUser().pipe(
          map((response) => AuthActions.checkAuthSuccess({ response })),
          catchError((error) => of(AuthActions.checkAuthFailure(error)))
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
  saveTokenAndRefreshToken$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.loginSuccess, AuthActions.refreshTokenSuccess),
        tap((action) => {
          localStorage.setItem('token', action.response.token);
          localStorage.setItem('refreshToken', action.response.refreshToken);
        })
      ),
    { dispatch: false }
  );
  signUp$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.signUp),
      switchMap((action) =>
        this.authService.signUp(action.request).pipe(
          map((response) => AuthActions.signUpSuccess({ response })),
          tap(() => this.router.navigate(['/auth/login'])),
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
          tap(() => this.router.navigate(['/auth/login'])),
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
          tap(() => this.router.navigate(['/auth/login'])),
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
          tap(() => this.router.navigate(['/'])),
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
          tap(() => this.router.navigate(['/'])),
          catchError((error) => of(AuthActions.logoutFromAllDevicesFailure(error)))
        )
      )
    )
  );
  removeTokenAndRefreshToken$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.logoutSuccess, AuthActions.logoutFromAllDevicesSuccess),
        tap((action) => {
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
        })
      ),
    { dispatch: false }
  );
}
