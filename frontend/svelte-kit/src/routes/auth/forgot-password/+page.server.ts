import { makeRequest } from '$lib/server/apis/api';
import { apiErrors, HttpRequest } from '$lib/server/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { Actions, PageServerLoad } from './$types';
import { forgotPasswordSchema } from './forgot-password-schema';

export const load = (async ({ locals }) => {
  if (locals.userId) redirect(302, '/');

  return {
    form: await superValidate(zod(forgotPasswordSchema)),
  };
}) satisfies PageServerLoad;

export const actions = {
  forgotPassword: async ({ request }) => {
    const form = await superValidate(request, zod(forgotPasswordSchema));
    if (!form.valid) return fail(400, { form });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/password/forgot',
      body: JSON.stringify(form.data),
    });

    if ('error' in response) return apiErrors(response, form);

    redirect(302, '/auth/sign-in');
  },
} satisfies Actions;
