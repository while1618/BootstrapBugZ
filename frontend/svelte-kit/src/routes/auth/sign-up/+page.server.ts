import type { ErrorMessage } from '$lib/models/error-message';
import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

export const actions = {
  signUp: async ({ fetch, request }) => {
    const formData = await request.formData();
    const response = await fetch(`http://localhost:8080/v1/auth/sign-up`, {
      method: 'POST',
      body: JSON.stringify(signUpRequest(formData)),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 201) {
      const errorMessage = (await response.json()) as ErrorMessage;
      return fail(response.status, { errorMessage });
    }

    throw redirect(302, '/auth/sign-in');
  },
} satisfies Actions;

const signUpRequest = (request: FormData) => {
  return {
    firstName: request.get('firstName'),
    lastName: request.get('lastName'),
    username: request.get('username'),
    email: request.get('email'),
    password: request.get('password'),
    confirmPassword: request.get('confirmPassword'),
  };
};
