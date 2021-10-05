import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
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
  API_URL = 'http://localhost:8181/v1.0/auth';
  private jwtHelper: JwtHelperService;

  constructor(private http: HttpClient) {
    this.jwtHelper = new JwtHelperService();
  }

  getLoggedInUser() {
    return this.http.get<User>(`${this.API_URL}/logged-in-user`);
  }

  login(loginRequest: LoginRequest) {
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, loginRequest);
  }

  refreshToken(refreshTokenRequest: RefreshTokenRequest) {
    return this.http.post<RefreshTokenResponse>(
      `${this.API_URL}/refresh-token`,
      refreshTokenRequest
    );
  }

  signUp(signUpRequest: SignUpRequest) {
    return this.http.post<User>(`${this.API_URL}/sign-up`, signUpRequest);
  }

  resendConfirmationEmail(resendConfirmationEmailRequest: ResendConfirmationEmailRequest) {
    return this.http.post<void>(
      `${this.API_URL}/resend-confirmation-email`,
      resendConfirmationEmailRequest
    );
  }

  forgotPassword(forgotPasswordRequest: ForgotPasswordRequest) {
    return this.http.post<void>(`${this.API_URL}/forgot-password`, forgotPasswordRequest);
  }

  resetPassword(resetPasswordRequest: ResetPasswordRequest) {
    return this.http.put<void>(`${this.API_URL}/reset-password`, resetPasswordRequest);
  }

  logout() {
    return this.http.get<void>(`${this.API_URL}/logout`);
  }

  logoutFromAllDevices() {
    return this.http.get<void>(`${this.API_URL}/logout-from-all-devices`);
  }

  usernameAvailability(username: string) {
    return this.http.get<boolean>(`${this.API_URL}/username-availability`, {
      params: { username },
    });
  }

  emailAvailability(email: string) {
    return this.http.get<boolean>(`${this.API_URL}/email-availability`, {
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
