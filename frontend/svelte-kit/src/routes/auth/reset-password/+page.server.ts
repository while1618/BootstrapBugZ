import { API_URL } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import type { ErrorMessage } from '$lib/models/error-message';
import { PASSWORD_REGEX } from '$lib/regex/regex';
import { isObjectEmpty } from '$lib/utils/util';
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
  resetPassword: async ({ fetch, request, url }) => {
    const formData = await request.formData();
    const resetPasswordRequest = getResetPasswordRequest(formData, url);
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

    throw redirect(303, '/auth/sign-in');
  },
} satisfies Actions;

const getResetPasswordRequest = (request: FormData, url: URL): ResetPasswordRequest => {
  return {
    accessToken: url.searchParams.get('accessToken'),
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
  //TODO: check if token is JWT
  if (!PASSWORD_REGEX.test(request.password)) errors.password = en['password.invalid'];
  if (!PASSWORD_REGEX.test(request.confirmPassword))
    errors.confirmPassword = en['password.invalid'];
  if (request.password !== request.confirmPassword)
    errors.confirmPassword = en['password.doNotMatch'];

  return errors;
};
