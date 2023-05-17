import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { error, redirect } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const GET = (async ({ locals, url }) => {
  if (locals.userId) throw redirect(302, '/');

  const token = url.searchParams.get('token');
  const response = await makeRequest({
    method: HttpRequest.PUT,
    path: '/auth/confirm-registration',
    body: JSON.stringify({ token }),
  });

  if ('error' in response) throw error(response.status, { message: response.error });

  throw redirect(302, '/auth/sign-in');
}) satisfies RequestHandler;
