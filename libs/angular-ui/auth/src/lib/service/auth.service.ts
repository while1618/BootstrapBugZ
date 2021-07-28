import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '@bootstrapbugz/angular-ui/user';
import {
  ForgotPasswordRequest,
  LoginRequest,
  RefreshTokenRequest,
  ResendConfirmationEmailRequest,
  ResetPasswordRequest,
  SignUpRequest,
} from '../model/auth.requests';
import { LoginResponse, RefreshTokenResponse } from '../model/auth.responses';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  API_URL = 'http://localhost:8181/v1.0/auth';

  constructor(private http: HttpClient) {}

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
}
