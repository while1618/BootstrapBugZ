import { API_URL } from '$lib/apis/api';
import type { UserDTO } from '$lib/models/user';
import type { PageServerLoad } from './$types';

export const load = (async ({ fetch, cookies, locals }) => {
  if (!locals.userId) return { user: null };

  const response = await fetch(`${API_URL}/auth/signed-in-user`, {
    headers: {
      Authorization: cookies.get('accessToken') || '',
      'Content-Type': 'application/json',
    },
  });

  if (response.status !== 200) return { user: null };

  const user = (await response.json()) as UserDTO;
  return { user };
}) satisfies PageServerLoad;
