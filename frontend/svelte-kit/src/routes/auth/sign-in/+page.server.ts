import en from '$lib/i18n/en.json';
import type { AuthTokensDTO } from '$lib/models/auth/auth-tokens';
import { EMAIL_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from '$lib/regex/regex';
import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest, setAccessTokenCookie, setRefreshTokenCookie } from '$lib/server/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import { z } from 'zod';
import type { Actions, PageServerLoad } from './$types';

export const load = (({ locals }) => {
  if (locals.userId) throw redirect(302, '/');
}) satisfies PageServerLoad;

const signInSchema = z.object({
  usernameOrEmail: z
    .string()
    .refine(
      (value) => USERNAME_REGEX.test(value) || EMAIL_REGEX.test(value),
      en['usernameOrEmail.invalid'],
    ),
  password: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
});

export const actions = {
  signIn: async ({ request, cookies }) => {
    const formData = Object.fromEntries(await request.formData());
    const signInForm = signInSchema.safeParse(formData);
    if (!signInForm.success) return fail(400, { errors: signInForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/tokens',
      body: JSON.stringify(signInForm.data),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    const { accessToken, refreshToken } = response as AuthTokensDTO;
    setAccessTokenCookie(cookies, accessToken);
    setRefreshTokenCookie(cookies, refreshToken);

    throw redirect(302, '/');
  },
} satisfies Actions;
