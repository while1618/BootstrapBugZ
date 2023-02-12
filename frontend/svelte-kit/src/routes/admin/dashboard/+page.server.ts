import { API_URL } from '$lib/apis/api';
import type { ErrorMessage } from '$lib/models/error-message';
import type { UserDTO } from '$lib/models/user';
import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load = (async ({ cookies, fetch }) => {
  const response = await fetch(`${API_URL}/users`, {
    method: 'GET',
    headers: {
      Authorization: cookies.get('accessToken') || '',
      'Content-Type': 'application/json',
    },
  });

  if (response.status !== 200) {
    const errorMessage = (await response.json()) as ErrorMessage;
    console.log(errorMessage);
    throw error(response.status, errorMessage.error);
  }

  const users = (await response.json()) as UserDTO[];

  return { users };
}) satisfies PageServerLoad;
