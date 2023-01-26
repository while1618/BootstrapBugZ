import type { ErrorMessage } from '$lib/models/error-message';
import type { SignInDTO } from '$lib/models/sign-in';
import { fail, redirect } from '@sveltejs/kit';
import { signedInUser } from '../../../stores/user';
import type { Actions } from './$types';

export const actions = {
  signIn: async ({ fetch, request }) => {
    const formData = await request.formData();
    const response = await fetch(`http://localhost:8080/v1/auth/sign-in`, {
      method: 'POST',
      body: JSON.stringify(signInRequest(formData)),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 200) {
      const errorMessage = (await response.json()) as ErrorMessage;
      return fail(response.status, { errorMessage });
    }

    const signInDTO = (await response.json()) as SignInDTO;
    signedInUser.set(signInDTO.user);

    throw redirect(302, '/');
  },
} satisfies Actions;

const signInRequest = (formData: FormData) => {
  return {
    usernameOrEmail: formData.get('usernameOrEmail'),
    password: formData.get('password'),
  };
};
