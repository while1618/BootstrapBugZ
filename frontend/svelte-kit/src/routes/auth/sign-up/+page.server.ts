import { API_URL } from '$lib/apis/api';
import type { ErrorMessage } from '$lib/models/error-message';
import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

interface SignUpRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export const actions = {
  signUp: async ({ fetch, request }) => {
    const formData = await request.formData();
    const signUpRequest = getSignUpRequest(formData);
    const response = await fetch(`${API_URL}/auth/sign-up`, {
      method: 'POST',
      body: JSON.stringify(signUpRequest),
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

const getSignUpRequest = (request: FormData): SignUpRequest => {
  return {
    firstName: request.get('firstName'),
    lastName: request.get('lastName'),
    username: request.get('username'),
    email: request.get('email'),
    password: request.get('password'),
    confirmPassword: request.get('confirmPassword'),
  } as SignUpRequest;
};
