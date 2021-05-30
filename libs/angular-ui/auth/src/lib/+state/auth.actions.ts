import {
  ForgotPasswordRequest,
  LoginRequest,
  ResendConfirmationEmailRequest,
  ResetPasswordRequest,
  SignUpRequest,
  User,
} from '@bootstrapbugz/shared';
import { createAction, props } from '@ngrx/store';

const LOGIN = '[Auth] Login';
const LOGIN_SUCCESS = '[Auth] Login Success';
const LOGIN_FAILURE = '[Auth] Login Failure';
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

export const login = createAction(LOGIN, props<{ loginRequest: LoginRequest }>());

export const loginSuccess = createAction(LOGIN_SUCCESS, props<{ token: string }>());

export const loginFailure = createAction(LOGIN_FAILURE, props<{ error: Error }>());

export const signUp = createAction(SIGN_UP, props<{ signUpRequest: SignUpRequest }>());

export const signUpSuccess = createAction(SIGN_UP_SUCCESS, props<{ user: User }>());

export const signUpFailure = createAction(SIGN_UP_FAILURE, props<{ error: Error }>());

export const confirmRegistration = createAction(CONFIRM_REGISTRATION);

export const confirmRegistrationSuccess = createAction(CONFIRM_REGISTRATION_SUCCESS);

export const confirmRegistrationFailure = createAction(
  CONFIRM_REGISTRATION_FAILURE,
  props<{ error: Error }>()
);

export const resendConfirmationEmail = createAction(
  RESEND_CONFIRMATION_EMAIL,
  props<{ resendConfirmationEmailRequest: ResendConfirmationEmailRequest }>()
);

export const resendConfirmationEmailSuccess = createAction(RESEND_CONFIRMATION_EMAIL_SUCCESS);

export const resendConfirmationEmailFailure = createAction(
  RESEND_CONFIRMATION_EMAIL_FAILURE,
  props<{ error: Error }>()
);

export const forgotPassword = createAction(
  FORGOT_PASSWORD,
  props<{ forgotPasswordRequest: ForgotPasswordRequest }>()
);

export const forgotPasswordSuccess = createAction(FORGOT_PASSWORD_SUCCESS);

export const forgotPasswordFailure = createAction(
  FORGOT_PASSWORD_FAILURE,
  props<{ error: Error }>()
);

export const resetPassword = createAction(
  RESET_PASSWORD,
  props<{ resetPasswordRequest: ResetPasswordRequest }>()
);

export const resetPasswordSuccess = createAction(RESET_PASSWORD_SUCCESS);

export const resetPasswordFailure = createAction(RESET_PASSWORD_FAILURE, props<{ error: Error }>());

export const logout = createAction(LOGOUT);

export const logoutSuccess = createAction(LOGOUT_SUCCESS);

export const logoutFailure = createAction(LOGOUT_FAILURE, props<{ error: Error }>());
