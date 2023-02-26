import { HttpRequest, makeRequest } from '$lib/apis/api';
import type { ErrorMessage } from '$lib/models/error-message';
import type { UserDTO } from '$lib/models/user';
import { error, fail, type Cookies } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';

export const load = (async ({ cookies }) => {
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: '/users',
    auth: cookies.get('accessToken') || '',
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
  activate: async ({ cookies, url }) => {
    await performAction('activate', cookies, url);
  },
  deactivate: async ({ cookies, url }) => {
    await performAction('deactivate', cookies, url);
  },
  lock: async ({ cookies, url }) => {
    await performAction('lock', cookies, url);
  },
  unlock: async ({ cookies, url }) => {
    await performAction('unlock', cookies, url);
  },
  delete: async ({ cookies, url }) => {
    const username = url.searchParams.get('username');
    const response = await makeRequest({
      method: HttpRequest.PUT,
      path: '/admin/users/delete',
      body: JSON.stringify({ usernames: [username] }),
      auth: cookies.get('accessToken') || '',
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
  const response = await makeRequest({
    method: HttpRequest.PUT,
    path: `/admin/users/${path}`,
    body: JSON.stringify({ usernames: [username] }),
    auth: cookies.get('accessToken') || '',
  });

  if (response.status !== 204) {
    const errorMessage = (await response.json()) as ErrorMessage;
    console.log(errorMessage);
    return fail(response.status, { errorMessage });
  }
};
