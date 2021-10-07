import { createAction, props } from '@ngrx/store';
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

// LOGIN
export const login = createAction('[Auth] Login', props<{ request: LoginRequest }>());
export const loginSuccess = createAction(
  '[Auth] Login Success',
  props<{ response: LoginResponse }>()
);
export const loginFailure = createAction('[Auth] Login Failure', props<{ error: Error }>());
// CHECK AUTH
export const checkAuth = createAction('[Auth] Check Auth');
export const checkAuthSuccess = createAction(
  '[Auth] Check Auth Success',
  props<{ response: User }>()
);
export const checkAuthFailure = createAction(
  '[Auth] Check Auth Failure',
  props<{ error: Error }>()
);
// REFRESH TOKEN
export const refreshToken = createAction(
  '[Auth] Refresh Token',
  props<{ request: RefreshTokenRequest }>()
);
export const refreshTokenSuccess = createAction(
  '[Auth] Refresh Token Success',
  props<{ response: RefreshTokenResponse }>()
);
export const refreshTokenFailure = createAction(
  '[Auth] Refresh Token Failure',
  props<{ error: Error }>()
);
// SIGN UP
export const signUp = createAction('[Auth] SignUp', props<{ request: SignUpRequest }>());
export const signUpSuccess = createAction('[Auth] SignUp Success', props<{ response: User }>());
export const signUpFailure = createAction('[Auth] SignUp Failure', props<{ error: Error }>());
// CONFIRM REGISTRATION
export const confirmRegistration = createAction(
  '[Auth] Confirm Registration',
  props<{ request: string }>()
);
export const confirmRegistrationSuccess = createAction('[Auth] Confirm Registration Success');
export const confirmRegistrationFailure = createAction(
  '[Auth] Confirm Registration Failure',
  props<{ error: Error }>()
);
// RESEND CONFIRMATION EMAIL
export const resendConfirmationEmail = createAction(
  '[Auth] Resend Confirmation Email',
  props<{ request: ResendConfirmationEmailRequest }>()
);
export const resendConfirmationEmailSuccess = createAction(
  '[Auth] Resend Confirmation Email Success'
);
export const resendConfirmationEmailFailure = createAction(
  '[Auth] Resend Confirmation Email Failure',
  props<{ error: Error }>()
);
// FORGOT PASSWORD
export const forgotPassword = createAction(
  '[Auth] Forgot Password',
  props<{ request: ForgotPasswordRequest }>()
);
export const forgotPasswordSuccess = createAction('[Auth] Forgot Password Success');
export const forgotPasswordFailure = createAction(
  '[Auth] Forgot Password Failure',
  props<{ error: Error }>()
);
// RESET PASSWORD
export const resetPassword = createAction(
  '[Auth] Reset Password',
  props<{ request: ResetPasswordRequest }>()
);
export const resetPasswordSuccess = createAction('[Auth] Reset Password Success');
export const resetPasswordFailure = createAction(
  '[Auth] Reset Password Failure',
  props<{ error: Error }>()
);
// LOGOUT
export const logout = createAction('[Auth] Logout');
export const logoutSuccess = createAction('[Auth] Logout Success');
export const logoutFailure = createAction('[Auth] Logout Failure', props<{ error: Error }>());
// LOGOUT FROM ALL DEVICES
export const logoutFromAllDevices = createAction('[Auth] Logout From All Devices');
export const logoutFromAllDevicesSuccess = createAction('[Auth] Logout From All Devices Success');
export const logoutFromAllDevicesFailure = createAction(
  '[Auth] Logout From All Devices Failure',
  props<{ error: Error }>()
);
