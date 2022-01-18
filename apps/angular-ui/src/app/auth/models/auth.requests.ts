export interface SignUpRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface ResendConfirmationEmailRequest {
  usernameOrEmail: string;
}

export interface SignInRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  accessToken: string;
  password: string;
  confirmPassword: string;
}
