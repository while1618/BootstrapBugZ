import type { Availability } from '$lib/models/shared/availability';
import * as m from '$lib/paraglide/messages.js';
import { apiErrors, makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import { setError, superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { Actions, PageServerLoad } from './$types';
import { signUpSchema } from './schema';

export const load = (async ({ locals }) => {
  if (locals.userId) redirect(302, '/');

  return {
    form: await superValidate(zod(signUpSchema)),
  };
}) satisfies PageServerLoad;

export const actions = {
  signUp: async ({ request }) => {
    const form = await superValidate(request, zod(signUpSchema));
    if (!form.valid) return fail(400, { form });

    if (!(await usernameAvailability(form.data.username))) {
      setError(form, 'username', m.auth_usernameExists());
      form.valid = false;
    }
    if (!(await emailAvailability(form.data.email))) {
      setError(form, 'email', m.auth_emailExists());
      form.valid = false;
    }
    if (!form.valid) return fail(409, { form });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/register',
      body: JSON.stringify(form.data),
    });

    if ('error' in response) return apiErrors(response, form);

    redirect(302, '/auth/sign-in');
  },
} satisfies Actions;

const usernameAvailability = async (username: string) => {
  const response = await makeRequest({
    method: HttpRequest.POST,
    path: '/users/username/availability',
    body: JSON.stringify({ username }),
  });
  return (response as Availability).available;
};

const emailAvailability = async (email: string) => {
  const response = await makeRequest({
    method: HttpRequest.POST,
    path: '/users/email/availability',
    body: JSON.stringify({ email }),
  });
  return (response as Availability).available;
};
