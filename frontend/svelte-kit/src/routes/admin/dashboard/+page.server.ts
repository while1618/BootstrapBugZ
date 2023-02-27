import { HttpRequest, makeRequest } from '$lib/apis/api';
import type { UserDTO } from '$lib/models/user';
import { error, fail, type Cookies } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';

export const load = (async ({ cookies }) => {
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: '/users',
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) throw error(response.status, String(response.error));

  const users = response as UserDTO[];
  users.sort((a, b) => a.id - b.id);

  return { users };
}) satisfies PageServerLoad;

export const actions = {
  activate: ({ cookies, url }) => {
    performAction(HttpRequest.PUT, 'activate', cookies, url);
  },
  deactivate: ({ cookies, url }) => {
    performAction(HttpRequest.PUT, 'deactivate', cookies, url);
  },
  lock: ({ cookies, url }) => {
    performAction(HttpRequest.PUT, 'lock', cookies, url);
  },
  unlock: ({ cookies, url }) => {
    performAction(HttpRequest.PUT, 'unlock', cookies, url);
  },
  delete: ({ cookies, url }) => {
    performAction(HttpRequest.DELETE, 'delete', cookies, url);
  },
} satisfies Actions;

const performAction = async (method: HttpRequest, path: string, cookies: Cookies, url: URL) => {
  const username = url.searchParams.get('username');
  const response = await makeRequest({
    method,
    path: `/admin/users/${path}`,
    body: JSON.stringify({ usernames: [username] }),
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) return fail(response.status, { errorMessage: response });
};
