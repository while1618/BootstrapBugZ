export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

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

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  password: string;
  confirmPassword: string;
}
