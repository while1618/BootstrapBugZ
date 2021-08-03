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

const LOGIN = '[Auth] Login';
const LOGIN_SUCCESS = '[Auth] Login Success';
const LOGIN_FAILURE = '[Auth] Login Failure';
const REFRESH_TOKEN = '[Auth] Refresh Token';
const REFRESH_TOKEN_SUCCESS = '[Auth] Refresh Token Success';
const REFRESH_TOKEN_FAILURE = '[Auth] Refresh Token Failure';
const SIGN_UP = '[Auth] SignUp';
const SIGN_UP_SUCCESS = '[Auth] SignUp Success';
const SIGN_UP_FAILURE = '[Auth] SignUp Failure';
const CONFIRM_REGISTRATION = '[Auth] Confirm Registration';
const CONFIRM_REGISTRATION_SUCCESS = '[Auth] Confirm Registration Success';
const CONFIRM_REGISTRATION_FAILURE = '[Auth] Confirm Registration Failure';
const RESEND_CONFIRMATION_EMAIL = '[Auth] Resend Confirmation Email';
const RESEND_CONFIRMATION_EMAIL_SUCCESS = '[Auth] Resend Confirmation Email Success';
const RESEND_CONFIRMATION_EMAIL_FAILURE = '[Auth] Resend Confirmation Email Failure';
const FORGOT_PASSWORD = '[Auth] Forgot Password';
const FORGOT_PASSWORD_SUCCESS = '[Auth] Forgot Password Success';
const FORGOT_PASSWORD_FAILURE = '[Auth] Forgot Password Failure';
const RESET_PASSWORD = '[Auth] Reset Password';
const RESET_PASSWORD_SUCCESS = '[Auth] Reset Password Success';
const RESET_PASSWORD_FAILURE = '[Auth] Reset Password Failure';
const LOGOUT = '[Auth] Logout';
const LOGOUT_SUCCESS = '[Auth] Logout Success';
const LOGOUT_FAILURE = '[Auth] Logout Failure';
const LOGOUT_FROM_ALL_DEVICES = '[Auth] Logout From All Devices';
const LOGOUT_FROM_ALL_DEVICES_SUCCESS = '[Auth] Logout From All Devices Success';
const LOGOUT_FROM_ALL_DEVICES_FAILURE = '[Auth] Logout From All Devices Failure';

export const login = createAction(LOGIN, props<{ request: LoginRequest }>());
export const loginSuccess = createAction(LOGIN_SUCCESS, props<{ response: LoginResponse }>());
export const loginFailure = createAction(LOGIN_FAILURE, props<{ error: Error }>());

export const refreshToken = createAction(REFRESH_TOKEN, props<{ request: RefreshTokenRequest }>());
export const refreshTokenSuccess = createAction(
  REFRESH_TOKEN_SUCCESS,
  props<{ response: RefreshTokenResponse }>()
);
export const refreshTokenFailure = createAction(REFRESH_TOKEN_FAILURE, props<{ error: Error }>());

export const signUp = createAction(SIGN_UP, props<{ request: SignUpRequest }>());
export const signUpSuccess = createAction(SIGN_UP_SUCCESS, props<{ response: User }>());
export const signUpFailure = createAction(SIGN_UP_FAILURE, props<{ error: Error }>());

export const confirmRegistration = createAction(CONFIRM_REGISTRATION);
export const confirmRegistrationSuccess = createAction(CONFIRM_REGISTRATION_SUCCESS);
export const confirmRegistrationFailure = createAction(
  CONFIRM_REGISTRATION_FAILURE,
  props<{ error: Error }>()
);

export const resendConfirmationEmail = createAction(
  RESEND_CONFIRMATION_EMAIL,
  props<{ request: ResendConfirmationEmailRequest }>()
);
export const resendConfirmationEmailSuccess = createAction(RESEND_CONFIRMATION_EMAIL_SUCCESS);
export const resendConfirmationEmailFailure = createAction(
  RESEND_CONFIRMATION_EMAIL_FAILURE,
  props<{ error: Error }>()
);

export const forgotPassword = createAction(
  FORGOT_PASSWORD,
  props<{ request: ForgotPasswordRequest }>()
);
export const forgotPasswordSuccess = createAction(FORGOT_PASSWORD_SUCCESS);
export const forgotPasswordFailure = createAction(
  FORGOT_PASSWORD_FAILURE,
  props<{ error: Error }>()
);

export const resetPassword = createAction(
  RESET_PASSWORD,
  props<{ request: ResetPasswordRequest }>()
);
export const resetPasswordSuccess = createAction(RESET_PASSWORD_SUCCESS);
export const resetPasswordFailure = createAction(RESET_PASSWORD_FAILURE, props<{ error: Error }>());

export const logout = createAction(LOGOUT);
export const logoutSuccess = createAction(LOGOUT_SUCCESS);
export const logoutFailure = createAction(LOGOUT_FAILURE, props<{ error: Error }>());

export const logoutFromAllDevices = createAction(LOGOUT_FROM_ALL_DEVICES);
export const logoutFromAllDevicesSuccess = createAction(LOGOUT_FROM_ALL_DEVICES_SUCCESS);
export const logoutFromAllDevicesFailure = createAction(
  LOGOUT_FROM_ALL_DEVICES_FAILURE,
  props<{ error: Error }>()
);
