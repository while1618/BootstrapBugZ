import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { API_URL_AUTH } from '../../shared/constants/paths';
import { User } from '../../user/models/user.models';
import {
  ForgotPasswordRequest,
  LoginRequest,
  RefreshTokenRequest,
  ResendConfirmationEmailRequest,
  ResetPasswordRequest,
  SignUpRequest,
} from '../models/auth.requests';
import { LoginResponse, RefreshTokenResponse } from '../models/auth.responses';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private jwtHelper: JwtHelperService;

  constructor(private http: HttpClient) {
    this.jwtHelper = new JwtHelperService();
  }

  getLoggedInUser() {
    return this.http.get<User>(`${API_URL_AUTH}/logged-in-user`);
  }

  login(loginRequest: LoginRequest) {
    return this.http.post<LoginResponse>(`${API_URL_AUTH}/login`, loginRequest);
  }

  refreshToken(refreshTokenRequest: RefreshTokenRequest) {
    return this.http.post<RefreshTokenResponse>(
      `${API_URL_AUTH}/refresh-token`,
      refreshTokenRequest
    );
  }

  signUp(signUpRequest: SignUpRequest) {
    return this.http.post<User>(`${API_URL_AUTH}/sign-up`, signUpRequest);
  }

  confirmRegistration(token: string) {
    return this.http.get<void>(`${API_URL_AUTH}/confirm-registration`, {
      params: { token },
    });
  }

  resendConfirmationEmail(resendConfirmationEmailRequest: ResendConfirmationEmailRequest) {
    return this.http.post<void>(
      `${API_URL_AUTH}/resend-confirmation-email`,
      resendConfirmationEmailRequest
    );
  }

  forgotPassword(forgotPasswordRequest: ForgotPasswordRequest) {
    return this.http.post<void>(`${API_URL_AUTH}/forgot-password`, forgotPasswordRequest);
  }

  resetPassword(resetPasswordRequest: ResetPasswordRequest) {
    return this.http.put<void>(`${API_URL_AUTH}/reset-password`, resetPasswordRequest);
  }

  logout() {
    return this.http.get<void>(`${API_URL_AUTH}/logout`);
  }

  logoutFromAllDevices() {
    return this.http.get<void>(`${API_URL_AUTH}/logout-from-all-devices`);
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

  getToken() {
    return localStorage.getItem('token');
  }

  getRefreshToken() {
    return localStorage.getItem('refreshToken');
  }

  isLoggedIn() {
    const token = this.getToken();
    const refreshToken = this.getRefreshToken();
    if (!token || !refreshToken) return false;
    try {
      return !this.jwtHelper.isTokenExpired(token) || !this.jwtHelper.isTokenExpired(refreshToken);
    } catch (e) {
      return false;
    }
  }

  isAdminLoggedIn() {
    const token = this.getToken();
    const refreshToken = this.getRefreshToken();
    if (!token || !refreshToken) return false;
    try {
      const decoded = this.jwtHelper.decodeToken(token);
      if (!decoded?.roles.includes('ADMIN')) return false;
      return !this.jwtHelper.isTokenExpired(token) || !this.jwtHelper.isTokenExpired(refreshToken);
    } catch (e) {
      return false;
    }
  }
}
