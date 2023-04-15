import { makeRequest } from '$lib/apis/api';
import { HttpRequest } from '$lib/utils/util';
import { error, redirect } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const GET = (async ({ locals, cookies }) => {
  const response = await makeRequest({
    method: HttpRequest.POST,
    path: '/auth/sign-out-from-all-devices',
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) throw error(response.status, { message: response.error });

  cookies.delete('accessToken', { path: '/' });
  cookies.delete('refreshToken', { path: '/' });
  locals.userId = null;

  throw redirect(302, '/');
}) satisfies RequestHandler;
