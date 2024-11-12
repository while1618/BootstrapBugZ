import { JWT_SECRET } from '$env/static/private';
import * as m from '$lib/paraglide/messages.js';
import { apiErrors, makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';
import { setError, superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { Actions, PageServerLoad } from './$types';
import { resetPasswordSchema } from './schema';

export const load = (async ({ locals, url }) => {
  if (locals.userId) redirect(302, '/');

  const initialData = { token: url.searchParams.get('token') ?? '' };
  const form = await superValidate(initialData, zod(resetPasswordSchema), { errors: false });
  return { form };
}) satisfies PageServerLoad;

export const actions = {
  resetPassword: async ({ request }) => {
    const form = await superValidate(request, zod(resetPasswordSchema));
    if (!form.valid) return fail(400, { form });

    try {
      jwt.verify(form.data.token, JWT_SECRET);
    } catch (_) {
      return setError(form, m.auth_tokenInvalid());
    }

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/password/reset',
      body: JSON.stringify(form.data),
    });

    if ('error' in response) return apiErrors(response, form);

    redirect(302, '/auth/sign-in');
  },
} satisfies Actions;
