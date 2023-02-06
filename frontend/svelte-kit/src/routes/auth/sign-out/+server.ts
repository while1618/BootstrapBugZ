import { API_URL } from '$lib/apis/api';
import type { ErrorMessage } from '$lib/models/error-message';
import { redirect } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const GET = (async ({ fetch, locals, cookies }) => {
  if (!locals.userId) throw redirect(302, '/');

  const response = await fetch(`${API_URL}/auth/sign-out`, {
    method: 'POST',
    headers: {
      Authorization: cookies.get('accessToken') || '',
      'Content-Type': 'application/json',
    },
  });

  if (response.status !== 204) {
    const errorMessage = (await response.json()) as ErrorMessage;
    console.log(errorMessage);
    return new Response(String(errorMessage));
  }

  cookies.delete('accessToken', { path: '/' });
  cookies.delete('refreshToken', { path: '/' });
  locals.userId = null;

  throw redirect(303, '/');
}) satisfies RequestHandler;
