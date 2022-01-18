import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import * as AuthActions from './auth.actions';

@Injectable()
export class AuthEffects {
  signUp$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.signUp),
      switchMap((action) =>
        this.authService.signUp(action.request).pipe(
          map((response) => AuthActions.signUpSuccess({ response })),
          tap(() => this.router.navigate(['/auth/sign-in'])),
          catchError((error) => of(AuthActions.signUpFailure(error)))
        )
      )
    )
  );
  confirmRegistration$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.confirmRegistration),
      switchMap((action) =>
        this.authService.confirmRegistration(action.request).pipe(
          map(() => AuthActions.confirmRegistrationSuccess()),
          tap(() => this.router.navigate(['/auth/sign-in'])),
          catchError((error) => of(AuthActions.confirmRegistrationFailure(error)))
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
  signIn$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.signIn),
      switchMap((action) =>
        this.authService.signIn(action.request).pipe(
          map((response) => AuthActions.signInSuccess({ response })),
          tap(() => this.router.navigate(['/'])),
          catchError((error) => of(AuthActions.signInFailure(error)))
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
  saveAccessTokenAndRefreshToken$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.signInSuccess, AuthActions.refreshTokenSuccess),
        tap((action) => {
          localStorage.setItem('accessToken', action.response.accessToken);
          localStorage.setItem('refreshToken', action.response.refreshToken);
        })
      ),
    { dispatch: false }
  );
  signOut$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.signOut),
      switchMap(() =>
        this.authService.signOut().pipe(
          map(() => AuthActions.signOutSuccess()),
          tap(() => this.router.navigate(['/'])),
          catchError((error) => of(AuthActions.signOutFailure(error)))
        )
      )
    )
  );
  signOutFromAllDevices$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.signOutFromAllDevices),
      switchMap(() =>
        this.authService.signOutFromAllDevices().pipe(
          map(() => AuthActions.signOutFromAllDevicesSuccess()),
          tap(() => this.router.navigate(['/'])),
          catchError((error) => of(AuthActions.signOutFromAllDevicesFailure(error)))
        )
      )
    )
  );
  removeAccessTokenAndRefreshToken$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.signOutSuccess, AuthActions.signOutFromAllDevicesSuccess),
        tap((action) => {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
        })
      ),
    { dispatch: false }
  );
  forgotPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.forgotPassword),
      switchMap((action) =>
        this.authService.forgotPassword(action.request).pipe(
          map(() => AuthActions.forgotPasswordSuccess()),
          tap(() => this.router.navigate(['/auth/sign-in'])),
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
          tap(() => this.router.navigate(['/auth/sign-in'])),
          catchError((error) => of(AuthActions.resetPasswordFailure(error)))
        )
      )
    )
  );
  checkAuth$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.checkAuth),
      switchMap((action) =>
        this.authService.getSignedInUser().pipe(
          map((response) => AuthActions.checkAuthSuccess({ response })),
          catchError((error) => of(AuthActions.checkAuthFailure(error)))
        )
      )
    )
  );

  constructor(
    private actions$: Actions,
    private authService: AuthService,
    private router: Router
  ) {}
}
