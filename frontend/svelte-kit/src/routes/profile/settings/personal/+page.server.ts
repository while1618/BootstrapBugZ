import { apiErrors, makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { fail, type Actions } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { PageServerLoad } from './$types';
import { updateProfileSchema } from './update-profile-schema';

export const load = (async ({ parent }) => {
  const { user } = await parent();
  const initialData = { username: user?.username, email: user?.email };
  const form = await superValidate(initialData, zod(updateProfileSchema));
  return { form };
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
