import type { AuthTokens } from '$lib/models/auth/auth-tokens';
import { makeRequest } from '$lib/server/apis/api';
import {
  apiErrors,
  HttpRequest,
  setAccessTokenCookie,
  setRefreshTokenCookie,
} from '$lib/server/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { Actions, PageServerLoad } from './$types';
import { signInSchema } from './sign-in-schema';

export const load = (async ({ locals }) => {
  if (locals.userId) redirect(302, '/');

  return {
    form: await superValidate(zod(signInSchema)),
  };
}) satisfies PageServerLoad;

export const actions = {
  signIn: async ({ request, cookies }) => {
    const form = await superValidate(request, zod(signInSchema));
    if (!form.valid) return fail(400, { form });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/tokens',
      body: JSON.stringify(form.data),
    });

    if ('error' in response) return apiErrors(response, form);

    const { accessToken, refreshToken } = response as AuthTokens;
    setAccessTokenCookie(cookies, accessToken);
    setRefreshTokenCookie(cookies, refreshToken);

    redirect(302, '/');
  },
} satisfies Actions;
