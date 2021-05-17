import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  ForgotPasswordRequest,
  LoginRequest,
  ResendConfirmationEmailRequest,
  ResetPasswordRequest,
  SignUpRequest,
} from '@bootstrapbugz/angular-ui/auth/data';
import { SimpleUser } from '@bootstrapbugz/angular-ui/user/data';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  API_URL = 'localhost:8181/v1.0/auth';

  constructor(private http: HttpClient) {}

  login(loginRequest: LoginRequest) {
    return this.http.post<void>(`${this.API_URL}/login`, loginRequest);
  }

  logout() {
    return this.http.get<void>(`${this.API_URL}/logout`);
  }

  signUp(signUpRequest: SignUpRequest) {
    return this.http.post<SimpleUser>(`${this.API_URL}/sign-up`, signUpRequest);
  }

  confirmRegistration(token: string) {
    const params = new HttpParams().set('token', token);
    return this.http.get(`${this.API_URL}/confirm-registration`, { params: params });
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
}
