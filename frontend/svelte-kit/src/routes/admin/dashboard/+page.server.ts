import { API_URL } from '$lib/apis/api';
import type { ErrorMessage } from '$lib/models/error-message';
import type { UserDTO } from '$lib/models/user';
import { error, fail, type Cookies } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';

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
  users.sort((a, b) => a.id - b.id);

  return { users };
}) satisfies PageServerLoad;

export const actions = {
  activate: async ({ fetch, cookies, url }) => {
    await performAction('activate', cookies, url);
  },
  deactivate: async ({ fetch, cookies, url }) => {
    await performAction('deactivate', cookies, url);
  },
  lock: async ({ fetch, cookies, url }) => {
    await performAction('lock', cookies, url);
  },
  unlock: async ({ fetch, cookies, url }) => {
    await performAction('unlock', cookies, url);
  },
  delete: async ({ fetch, cookies, url }) => {
    const username = url.searchParams.get('username');
    const response = await fetch(`${API_URL}/admin/users/delete`, {
      method: 'DELETE',
      body: JSON.stringify({ usernames: [username] }),
      headers: {
        Authorization: cookies.get('accessToken') || '',
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 204) {
      const errorMessage = (await response.json()) as ErrorMessage;
      console.log(errorMessage);
      return fail(response.status, { errorMessage });
    }
  },
} satisfies Actions;

const performAction = async (path: string, cookies: Cookies, url: URL) => {
  const username = url.searchParams.get('username');
  const response = await fetch(`${API_URL}/admin/users/${path}`, {
    method: 'PUT',
    body: JSON.stringify({ usernames: [username] }),
    headers: {
      Authorization: cookies.get('accessToken') || '',
      'Content-Type': 'application/json',
    },
  });

  if (response.status !== 204) {
    const errorMessage = (await response.json()) as ErrorMessage;
    console.log(errorMessage);
    return fail(response.status, { errorMessage });
  }
};
