import { HttpRequest, makeRequest } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import { PASSWORD_REGEX } from '$lib/regex/regex';
import { isObjectEmpty } from '$lib/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';

interface ResetPasswordRequest {
  token: string;
  password: string;
  confirmPassword: string;
}

interface ResetPasswordErrors {
  token: string | null;
  password: string | null;
  confirmPassword: string | null;
}

export const load = (({ locals }) => {
  if (locals.user) throw redirect(302, '/');
}) satisfies PageServerLoad;

export const actions = {
  resetPassword: async ({ request, url }) => {
    const formData = await request.formData();
    const resetPasswordRequest = getResetPasswordRequest(formData, url);
    const errors = checkResetPasswordRequest(resetPasswordRequest);
    if (!isObjectEmpty(errors)) return fail(400, { errors });

    const response = await makeRequest({
      method: HttpRequest.PUT,
      path: '/auth/reset-password',
      body: JSON.stringify(resetPasswordRequest),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    throw redirect(303, '/auth/sign-in');
  },
} satisfies Actions;

const getResetPasswordRequest = (request: FormData, url: URL): ResetPasswordRequest => {
  return {
    token: url.searchParams.get('token'),
    password: request.get('password'),
    confirmPassword: request.get('confirmPassword'),
  } as ResetPasswordRequest;
};

const checkResetPasswordRequest = (request: ResetPasswordRequest): ResetPasswordErrors => {
  const errors: ResetPasswordErrors = {
    token: null,
    password: null,
    confirmPassword: null,
  };

  if (request.token === '') errors.token = en['token.invalid'];
  if (!PASSWORD_REGEX.test(request.password)) errors.password = en['password.invalid'];
  if (!PASSWORD_REGEX.test(request.confirmPassword))
    errors.confirmPassword = en['password.invalid'];
  if (request.password !== request.confirmPassword)
    errors.confirmPassword = en['password.doNotMatch'];

  return errors;
};
