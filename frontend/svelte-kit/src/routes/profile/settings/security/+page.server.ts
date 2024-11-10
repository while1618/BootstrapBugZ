import { makeRequest } from '$lib/server/apis/api';
import { apiErrors, HttpRequest, removeAuth } from '$lib/server/utils/util';
import { error, fail, redirect, type Actions, type NumericRange } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { PageServerLoad } from './$types';
import { changePasswordSchema } from './change-password-schema';

export const load = (async () => {
  return {
    form: await superValidate(zod(changePasswordSchema)),
  };
}) satisfies PageServerLoad;

export const actions = {
  changePassword: async ({ request, cookies, locals }) => {
    const form = await superValidate(request, zod(changePasswordSchema));
    if (!form.valid) return fail(400, { form });

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: '/profile/password',
      body: JSON.stringify(form.data),
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return apiErrors(response, form);

    removeAuth(cookies, locals);
    redirect(302, '/');
  },
  delete: async ({ cookies, locals }) => {
    const response = await makeRequest({
      method: HttpRequest.DELETE,
      path: '/profile',
      auth: cookies.get('accessToken'),
    });

    if ('error' in response)
      error(response.status as NumericRange<400, 599>, { message: response.error });

    removeAuth(cookies, locals);
    redirect(302, '/');
  },
} satisfies Actions;
