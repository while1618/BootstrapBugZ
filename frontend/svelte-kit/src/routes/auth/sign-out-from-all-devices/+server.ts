import { HttpRequest, makeRequest } from '$lib/apis/api';
import { redirect } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const POST = (async ({ locals, cookies }) => {
  if (!locals.user) throw redirect(302, '/');

  const response = await makeRequest({
    method: HttpRequest.POST,
    path: '/auth/sign-out-from-all-devices',
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) return new Response(String(response.error));

  cookies.delete('accessToken', { path: '/' });
  cookies.delete('refreshToken', { path: '/' });
  locals.user = null;

  throw redirect(303, '/');
}) satisfies RequestHandler;
