import { HttpRequest, makeRequest } from '$lib/apis/api';
import { redirect } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const GET = (async ({ locals, cookies }) => {
  const response = await makeRequest({
    method: HttpRequest.POST,
    path: '/auth/sign-out',
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) return new Response(String(response.error));

  cookies.delete('accessToken', { path: '/' });
  cookies.delete('refreshToken', { path: '/' });
  locals.userId = null;

  throw redirect(303, '/');
}) satisfies RequestHandler;
