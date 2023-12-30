import type { UserDTO } from '$lib/models/user/user';
import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest, removeAuth } from '$lib/server/utils/util';
import { error, redirect, type NumericRange } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';

export const load = (async ({ locals, cookies }) => {
  if (!locals.userId) return { user: null };

  const response = await makeRequest({
    method: HttpRequest.GET,
    path: `/profile`,
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) {
    if (response.status === 401) {
      removeAuth(cookies, locals);
      redirect(302, '/');
    }
    error(response.status as NumericRange<400, 599>, { message: response.error });
  }

  return { user: response as UserDTO };
}) satisfies LayoutServerLoad;
