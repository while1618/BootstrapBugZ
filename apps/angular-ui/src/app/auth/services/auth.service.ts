import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { API_URL_AUTH } from '../../shared/constants/paths';
import { User } from '../../user/models/user.models';
import {
  ForgotPasswordRequest,
  RefreshTokenRequest,
  ResendConfirmationEmailRequest,
  ResetPasswordRequest,
  SignInRequest,
  SignUpRequest,
} from '../models/auth.requests';
import { RefreshTokenResponse, SignInResponse } from '../models/auth.responses';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private jwtHelper: JwtHelperService;

  constructor(private http: HttpClient) {
    this.jwtHelper = new JwtHelperService();
  }

  signUp(signUpRequest: SignUpRequest) {
    return this.http.post<User>(`${API_URL_AUTH}/sign-up`, signUpRequest);
  }

  resendConfirmationEmail(resendConfirmationEmailRequest: ResendConfirmationEmailRequest) {
    return this.http.post<void>(
      `${API_URL_AUTH}/resend-confirmation-email`,
      resendConfirmationEmailRequest
    );
  }

  confirmRegistration(accessToken: string) {
    return this.http.put<void>(`${API_URL_AUTH}/confirm-registration`, {
      params: { accessToken },
    });
  }

  signIn(signInRequest: SignInRequest) {
    return this.http.post<SignInResponse>(`${API_URL_AUTH}/sign-in`, signInRequest);
  }

  refreshToken(refreshTokenRequest: RefreshTokenRequest) {
    return this.http.post<RefreshTokenResponse>(
      `${API_URL_AUTH}/refresh-token`,
      refreshTokenRequest
    );
  }

  signOut() {
    return this.http.post<void>(`${API_URL_AUTH}/sign-out`, null);
  }

  signOutFromAllDevices() {
    return this.http.post<void>(`${API_URL_AUTH}/sign-out-from-all-devices`, null);
  }

  forgotPassword(forgotPasswordRequest: ForgotPasswordRequest) {
    return this.http.post<void>(`${API_URL_AUTH}/forgot-password`, forgotPasswordRequest);
  }

  resetPassword(resetPasswordRequest: ResetPasswordRequest) {
    return this.http.put<void>(`${API_URL_AUTH}/reset-password`, resetPasswordRequest);
  }

  getSignedInUser() {
    return this.http.get<User>(`${API_URL_AUTH}/signed-in-user`);
  }

  usernameAvailability(username: string) {
    return this.http.get<boolean>(`${API_URL_AUTH}/username-availability`, {
      params: { username },
    });
  }

  emailAvailability(email: string) {
    return this.http.get<boolean>(`${API_URL_AUTH}/email-availability`, {
      params: { email },
    });
  }

  getAccessToken() {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken() {
    return localStorage.getItem('refreshToken');
  }

  isSignedInAsUser() {
    const accessToken = this.getAccessToken();
    const refreshToken = this.getRefreshToken();
    if (!accessToken || !refreshToken) return false;
    try {
      return (
        !this.jwtHelper.isTokenExpired(accessToken) || !this.jwtHelper.isTokenExpired(refreshToken)
      );
    } catch (e) {
      return false;
    }
  }

  isSignedInAsAdmin() {
    const accessToken = this.getAccessToken();
    const refreshToken = this.getRefreshToken();
    if (!accessToken || !refreshToken) return false;
    try {
      const user = this.jwtHelper.decodeToken(accessToken);
      if (!user?.roles.includes('ADMIN')) return false;
      return (
        !this.jwtHelper.isTokenExpired(accessToken) || !this.jwtHelper.isTokenExpired(refreshToken)
      );
    } catch (e) {
      return false;
    }
  }
}
