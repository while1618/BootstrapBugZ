import type { ErrorMessage } from '$lib/models/error-message';
import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

export const actions = {
  resetPassword: async ({ fetch, request }) => {
    const formData = await request.formData();
    const response = await fetch(`http://localhost:8080/v1/auth/reset-password`, {
      method: 'POST',
      body: JSON.stringify(resetPasswordRequest(formData)),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 200) {
      const errorMessage = (await response.json()) as ErrorMessage;
      return fail(response.status, { errorMessage });
    }

    throw redirect(302, '/auth/sign-in');
  },
} satisfies Actions;

const resetPasswordRequest = (request: FormData) => {
  return {
    password: request.get('password'),
    confirmPassword: request.get('confirmPassword'),
  };
};
