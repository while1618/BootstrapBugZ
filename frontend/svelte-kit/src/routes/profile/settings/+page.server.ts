import type { AuthTokens } from '$lib/models/auth/auth-tokens';
import * as m from '$lib/paraglide/messages.js';
import { apiErrors, makeRequest } from '$lib/server/apis/api';
import {
  HttpRequest,
  removeAuth,
  setAccessTokenCookie,
  setRefreshTokenCookie,
} from '$lib/server/utils/util';
import { redirect } from '@sveltejs/kit';
import { fail, message, superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { Actions, PageServerLoad } from './$types';
import { changePasswordSchema, deleteSchema, updateProfileSchema } from './schema';

export const load = (async ({ parent }) => {
  const changePasswordForm = await superValidate(zod(changePasswordSchema));
  const deleteForm = await superValidate(zod(deleteSchema));
  const { user } = await parent();
  const updateProfileInitialData = { username: user?.username, email: user?.email };
  const updateProfileForm = await superValidate(updateProfileInitialData, zod(updateProfileSchema));
  return { updateProfileForm, changePasswordForm, deleteForm, user };
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

    return message(form, m.profile_updateSuccess());
  },
  changePassword: async ({ request, cookies, url }) => {
    const form = await superValidate(request, zod(changePasswordSchema));
    if (!form.valid) return fail(400, { form });

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: '/profile/password',
      body: JSON.stringify(form.data),
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return apiErrors(response, form);

    const signInResponse = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/tokens',
      body: JSON.stringify({
        usernameOrEmail: url.searchParams.get('username'),
        password: form.data.newPassword,
      }),
    });

    if ('error' in signInResponse) return apiErrors(signInResponse, form);

    const { accessToken, refreshToken } = signInResponse as AuthTokens;
    setAccessTokenCookie(cookies, accessToken);
    setRefreshTokenCookie(cookies, refreshToken);

    return message(form, m.profile_changePasswordSuccess());
  },
  delete: async ({ request, cookies, locals }) => {
    const form = await superValidate(request, zod(deleteSchema));

    const response = await makeRequest({
      method: HttpRequest.DELETE,
      path: '/profile',
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return apiErrors(response, form);

    removeAuth(cookies, locals);
    redirect(302, '/');
  },
} satisfies Actions;