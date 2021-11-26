import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, filter, Observable, take, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { RefreshTokenResponse } from '../models/auth.responses';
import { AuthService } from '../services/auth.service';
import { addAccessTokenToRequest } from '../util/util';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  private refreshTokenInProgress = false;
  // Refresh Token Subject tracks the current token, or is null if no token is currently
  // available (e.g. refresh pending).
  private refreshTokenSubject: BehaviorSubject<RefreshTokenResponse> =
    new BehaviorSubject<RefreshTokenResponse>(null);

  constructor(private router: Router, private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error) => {
        // We don't want to refresh token for some requests like login or refresh token itself
        // So we verify url and we throw an error if it's the case
        if (
          error.url.includes('login') ||
          error.url.includes('refresh-token') ||
          error.url.includes('logged-in-user')
        ) {
          // We do another check to see if refresh token failed
          // In this case we want to logout user and to redirect it to login page
          if (error.url.includes('refresh-token')) {
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
            this.router.navigateByUrl('/auth/login').then();
          }
          return throwError(() => error);
        }

        // If error status is different than 401 we want to skip refresh token
        // So we check that and throw the error if it's the case
        if (error.status !== 401) {
          return throwError(() => error);
        }

        if (this.refreshTokenInProgress) {
          // If refreshTokenInProgress is true, we will wait until refreshTokenSubject has a non-null value
          // – which means the new token is ready and we can retry the request again
          return this.refreshTokenSubject.pipe(
            filter((token: RefreshTokenResponse) => token !== null),
            take(1),
            switchMap(() => next.handle(this.addAuthToken(request)))
          );
        } else {
          this.refreshTokenInProgress = true;
          // Set the refreshTokenSubject to null so that subsequent API calls will wait until the new token has been retrieved
          this.refreshTokenSubject.next(null);

          const refreshToken = this.authService.getRefreshToken();
          // Call auth.refreshAccessToken(this is an Observable that will be returned)
          return this.authService.refreshToken({ refreshToken: refreshToken }).pipe(
            switchMap((token: RefreshTokenResponse) => {
              //When the call to refreshToken completes we reset the refreshTokenInProgress to false
              // for the next time the token needs to be refreshed
              this.refreshTokenInProgress = false;
              this.refreshTokenSubject.next(token);
              localStorage.setItem('token', token.token);
              localStorage.setItem('refreshToken', token.refreshToken);

              return next.handle(this.addAuthToken(request));
            }),
            catchError((err) => {
              this.refreshTokenInProgress = false;
              return throwError(() => err);
            })
          );
        }
      })
    );
  }

  private addAuthToken(request: HttpRequest<unknown>) {
    const accessToken = this.authService.getToken();
    if (!accessToken) return request;
    return addAccessTokenToRequest(request, accessToken);
  }
}
