import type { User } from '$lib/models/user/user';
import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest, isAdmin, removeAuth } from '$lib/server/utils/util';
import { error, redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';

export const load = (async ({ locals, cookies }) => {
  if (!locals.userId) return { user: null };

  const accessToken = cookies.get('accessToken');
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: `/profile`,
    auth: accessToken,
  });

  if ('error' in response) {
    if (response.status === 401) {
      removeAuth(cookies, locals);
      redirect(302, '/');
    }
    error(response.status, { message: response.error });
  }

  return { user: response as User, isAdmin: isAdmin(accessToken) };
}) satisfies LayoutServerLoad;
