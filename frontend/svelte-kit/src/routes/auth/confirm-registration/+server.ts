import { HttpRequest, makeRequest } from '$lib/apis/api';
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

  if ('error' in response)
    throw error(response.status, { message: response.error, status: response.status });

  throw redirect(302, '/auth/sign-in');
}) satisfies RequestHandler;
