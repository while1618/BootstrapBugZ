import { JWT_SECRET } from '$env/static/private';
import { HttpRequest, makeRequest } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import { PASSWORD_REGEX } from '$lib/regex/regex';
import { removeBearerPrefix } from '$lib/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';
import { z } from 'zod';
import type { Actions, PageServerLoad } from './$types';

export const load = (({ locals }) => {
  if (locals.userId) throw redirect(302, '/');
}) satisfies PageServerLoad;

const resetPasswordSchema = z
  .object({
    token: z.string().refine((value) => {
      try {
        jwt.verify(removeBearerPrefix(value), JWT_SECRET);
        return true;
      } catch (e) {
        return false;
      }
    }, en['token.invalid']),
    password: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
    confirmPassword: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
  })
  .superRefine(({ password, confirmPassword }, ctx) => {
    if (password !== confirmPassword) {
      ctx.addIssue({
        code: 'custom',
        message: en['password.doNotMatch'],
        path: ['confirmPassword'],
      });
    }
  });

export const actions = {
  resetPassword: async ({ request, url }) => {
    const formData = Object.fromEntries(await request.formData());
    const data = { ...formData, token: url.searchParams.get('token') };
    const resetPasswordForm = resetPasswordSchema.safeParse(data);
    if (!resetPasswordForm.success)
      return fail(400, { errors: resetPasswordForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.PUT,
      path: '/auth/reset-password',
      body: JSON.stringify(resetPasswordForm.data),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    throw redirect(302, '/auth/sign-in');
  },
} satisfies Actions;
