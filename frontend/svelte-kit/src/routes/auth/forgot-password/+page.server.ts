import { API_URL } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import type { ErrorMessage } from '$lib/models/error-message';
import { EMAIL_REGEX } from '$lib/regex/regex';
import { isObjectEmpty } from '$lib/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';

interface ForgotPasswordRequest {
  email: string;
}

interface ForgotPasswordErrors {
  email: string | null;
}

export const load = (({ locals }) => {
  if (locals.user) throw redirect(302, '/');
}) satisfies PageServerLoad;

export const actions = {
  forgotPassword: async ({ fetch, request }) => {
    const formData = await request.formData();
    const forgotPasswordRequest = getForgotPasswordRequest(formData);
    const errors = await checkForgotPasswordRequest(forgotPasswordRequest);
    if (!isObjectEmpty(errors)) return fail(400, { errors });

    const response = await fetch(`${API_URL}/auth/forgot-password`, {
      method: 'POST',
      body: JSON.stringify(forgotPasswordRequest),
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

const getForgotPasswordRequest = (request: FormData): ForgotPasswordRequest => {
  return {
    email: request.get('email'),
  } as ForgotPasswordRequest;
};

const checkForgotPasswordRequest = async (
  request: ForgotPasswordRequest
): Promise<ForgotPasswordErrors> => {
  const errors: ForgotPasswordErrors = {
    email: null,
  };

  if (request.email === '') errors.email = en['email.invalid'];
  if (!EMAIL_REGEX.test(request.email)) errors.email = en['email.invalid'];

  return errors;
};
