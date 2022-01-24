import { createAction, props } from '@ngrx/store';
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

export const signUp = createAction('[Auth] SignUp', props<{ request: SignUpRequest }>());
export const signUpSuccess = createAction('[Auth] SignUp Success', props<{ response: User }>());
export const signUpFailure = createAction('[Auth] SignUp Failure', props<{ error: Error }>());

export const confirmRegistration = createAction(
  '[Auth] Confirm Registration',
  props<{ request: string }>()
);
export const confirmRegistrationSuccess = createAction('[Auth] Confirm Registration Success');
export const confirmRegistrationFailure = createAction(
  '[Auth] Confirm Registration Failure',
  props<{ error: Error }>()
);

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

export const signIn = createAction('[Auth] Sign In', props<{ request: SignInRequest }>());
export const signInSuccess = createAction(
  '[Auth] Sign In Success',
  props<{ response: SignInResponse }>()
);
export const signInFailure = createAction('[Auth] Sign In Failure', props<{ error: Error }>());

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

export const signOut = createAction('[Auth] Sign Out');
export const signOutSuccess = createAction('[Auth] Sign Out Success');
export const signOutFailure = createAction('[Auth] Sign Out Failure', props<{ error: Error }>());

export const signOutFromAllDevices = createAction('[Auth] Sign Out From All Devices');
export const signOutFromAllDevicesSuccess = createAction(
  '[Auth] Sign Out From All Devices Success'
);
export const signOutFromAllDevicesFailure = createAction(
  '[Auth] Sign Out From All Devices Failure',
  props<{ error: Error }>()
);

export const forgotPassword = createAction(
  '[Auth] Forgot Password',
  props<{ request: ForgotPasswordRequest }>()
);
export const forgotPasswordSuccess = createAction('[Auth] Forgot Password Success');
export const forgotPasswordFailure = createAction(
  '[Auth] Forgot Password Failure',
  props<{ error: Error }>()
);

export const resetPassword = createAction(
  '[Auth] Reset Password',
  props<{ request: ResetPasswordRequest }>()
);
export const resetPasswordSuccess = createAction('[Auth] Reset Password Success');
export const resetPasswordFailure = createAction(
  '[Auth] Reset Password Failure',
  props<{ error: Error }>()
);

export const getSignedInUser = createAction('[Auth] Get Signed In User');
export const getSignedInUserSuccess = createAction(
  '[Auth] Get Signed In User Success',
  props<{ response: User }>()
);
export const getSignedInUserFailure = createAction(
  '[Auth] Get Signed In User Failure',
  props<{ error: Error }>()
);
