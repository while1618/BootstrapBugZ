import type { ErrorMessage } from '$lib/models/error-message';
import type { SignInDTO } from '$lib/models/sign-in';
import { fail } from '@sveltejs/kit';
import type { Actions } from './$types';

export const actions = {
  signIn: async ({ request }) => {
    const formData = await request.formData();
    const signInRequest = {
      usernameOrEmail: formData.get('usernameOrEmail'),
      password: formData.get('password'),
    };
    const response = await fetch(`http://localhost:8080/v1/auth/sign-in`, {
      method: 'POST',
      body: JSON.stringify(signInRequest),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 200) {
      const errorMessage = (await response.json()) as ErrorMessage;
      return fail(response.status, { errorMessage });
    }

    const signInDTO = (await response.json()) as SignInDTO;
    return { signInDTO };
  },
} satisfies Actions;
