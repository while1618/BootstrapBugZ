import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest, removeAuth } from '$lib/server/utils/util';
import { error, redirect } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const GET = (async ({ locals, cookies }) => {
  const response = await makeRequest({
    method: HttpRequest.DELETE,
    path: '/auth/tokens',
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) throw error(response.status, { message: response.error });

  removeAuth(cookies, locals);

  throw redirect(302, '/');
}) satisfies RequestHandler;
