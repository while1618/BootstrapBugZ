import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest, removeAuth } from '$lib/server/utils/util';
import { fail, redirect, type Actions } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { PageServerLoad } from './$types';
import { formSchema } from './schema';

export const load: PageServerLoad = async () => {
  return {
    form: await superValidate(zod(formSchema)),
  };
};

export const actions: Actions = {
  changePassword: async (event) => {
    const form = await superValidate(event, zod(formSchema));
    if (!form.valid) return fail(400, { form });
    console.log(JSON.stringify(form.data));
    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: '/profile/password',
      body: JSON.stringify(form.data),
      auth: event.cookies.get('accessToken'),
    });

    if ('error' in response) return fail(response.status, { form, apiErrors: response.codes });

    removeAuth(event.cookies, event.locals);
    redirect(302, '/');
  },
  delete: async ({ cookies, locals }) => {
    const response = await makeRequest({
      method: HttpRequest.DELETE,
      path: '/profile',
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return fail(response.status, { apiErrors: response.codes });

    removeAuth(cookies, locals);
    redirect(302, '/');
  },
};
