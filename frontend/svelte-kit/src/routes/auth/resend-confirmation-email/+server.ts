import { HttpRequest, makeRequest } from '$lib/apis/api';
import { redirect } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const GET = (async ({ locals, url }) => {
  if (locals.userId) throw redirect(302, '/');

  const usernameOrEmail = url.searchParams.get('usernameOrEmail');
  const response = await makeRequest({
    method: HttpRequest.POST,
    path: '/auth/resend-confirmation-email',
    body: JSON.stringify({ usernameOrEmail }),
  });

  if ('error' in response) return new Response(String(response.error));

  throw redirect(303, '/auth/sign-in');
}) satisfies RequestHandler;
