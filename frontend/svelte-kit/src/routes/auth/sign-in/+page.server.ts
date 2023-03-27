import { HttpRequest, makeRequest } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import type { SignInDTO } from '$lib/models/sign-in';
import { EMAIL_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from '$lib/regex/regex';
import { decodeJWT } from '$lib/utils/util';
import { fail, redirect, type Cookies } from '@sveltejs/kit';
import { z } from 'zod';
import type { Actions, PageServerLoad } from './$types';

const signInSchema = z.object({
  usernameOrEmail: z
    .string()
    .refine(
      (value) => USERNAME_REGEX.test(value) || EMAIL_REGEX.test(value),
      en['usernameOrEmail.invalid']
    ),
  password: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
});

export const load = (({ locals }) => {
  if (locals.user) throw redirect(302, '/');
}) satisfies PageServerLoad;

export const actions = {
  signIn: async ({ request, cookies }) => {
    const formData = Object.fromEntries(await request.formData());
    const signInForm = signInSchema.safeParse(formData);
    if (!signInForm.success) return fail(400, { errors: signInForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/sign-in',
      body: JSON.stringify(signInForm.data),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    const { accessToken, refreshToken } = response as SignInDTO;
    setAccessTokenCookie(cookies, accessToken);
    setRefreshTokenCookie(cookies, refreshToken);

    throw redirect(303, '/');
  },
} satisfies Actions;

const setAccessTokenCookie = (cookies: Cookies, accessToken: string): void => {
  const { exp } = decodeJWT(accessToken);
  cookies.set('accessToken', accessToken, {
    httpOnly: true,
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict',
    expires: new Date(exp * 1000),
  });
};

const setRefreshTokenCookie = (cookies: Cookies, refreshToken: string): void => {
  const { exp } = decodeJWT(refreshToken);
  cookies.set('refreshToken', refreshToken, {
    httpOnly: true,
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict',
    expires: new Date(exp * 1000),
  });
};
