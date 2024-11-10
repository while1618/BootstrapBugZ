import { makeRequest } from '$lib/server/apis/api';
import { apiErrors, HttpRequest } from '$lib/server/utils/util';
import { fail, type Actions } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { PageServerLoad } from './$types';
import { updateProfileSchema } from './update-profile-schema';

export const load = (async () => {
  return {
    form: await superValidate(zod(updateProfileSchema)),
  };
}) satisfies PageServerLoad;

export const actions = {
  updateProfile: async ({ request, cookies }) => {
    const form = await superValidate(request, zod(updateProfileSchema));
    if (!form.valid) return fail(400, { form });

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: '/profile',
      body: JSON.stringify(form.data),
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return apiErrors(response, form);
  },
} satisfies Actions;
