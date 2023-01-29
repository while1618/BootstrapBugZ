import { API_URL } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import type { ErrorMessage } from '$lib/models/error-message';
import { PASSWORD_REGEX } from '$lib/regex/regex';
import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

interface ResetPasswordRequest {
  accessToken: string;
  password: string;
  confirmPassword: string;
}

interface ResetPasswordErrors {
  accessToken: string | null;
  password: string | null;
  confirmPassword: string | null;
}

export const actions = {
  resetPassword: async ({ fetch, request }) => {
    const formData = await request.formData();
    const resetPasswordRequest = getResetPasswordRequest(formData);
    const errors = checkResetPasswordRequest(resetPasswordRequest);
    if (!isObjectEmpty(errors)) return fail(400, { errors });

    const response = await fetch(`${API_URL}/auth/reset-password`, {
      method: 'PUT',
      body: JSON.stringify(resetPasswordRequest),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 204) {
      const errorMessage = (await response.json()) as ErrorMessage;
      console.log(errorMessage);
      return fail(response.status, { errorMessage });
    }

    throw redirect(302, '/auth/sign-in');
  },
} satisfies Actions;

const getResetPasswordRequest = (request: FormData): ResetPasswordRequest => {
  return {
    accessToken: '',
    password: request.get('password'),
    confirmPassword: request.get('confirmPassword'),
  } as ResetPasswordRequest;
};

const checkResetPasswordRequest = (request: ResetPasswordRequest): ResetPasswordErrors => {
  const errors: ResetPasswordErrors = {
    accessToken: null,
    password: null,
    confirmPassword: null,
  };

  if (request.accessToken === '') errors.accessToken = en['token.invalid'];
  if (!PASSWORD_REGEX.test(request.password)) errors.password = en['password.invalid'];
  if (!PASSWORD_REGEX.test(request.confirmPassword))
    errors.confirmPassword = en['password.invalid'];
  if (request.password !== request.confirmPassword)
    errors.confirmPassword = en['password.doNotMatch'];

  return errors;
};

const isObjectEmpty = (obj: object): boolean => {
  const values = Object.values(obj);
  return values.every((val) => val === null);
};
