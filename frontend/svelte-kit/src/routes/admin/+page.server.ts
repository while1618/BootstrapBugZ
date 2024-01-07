import type { UserDTO } from '$lib/models/user/user';
import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { error, fail, type NumericRange } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';

export const load = (async ({ cookies }) => {
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: '/admin/users',
    auth: cookies.get('accessToken'),
  });

  if ('error' in response)
    error(response.status as NumericRange<400, 599>, { message: response.error });

  return { users: response as UserDTO[] };
}) satisfies PageServerLoad;

export const actions = {
  activate: async ({ cookies, url }) => {
    const id = url.searchParams.get('id');
    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ active: true }),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });
  },
  deactivate: async ({ cookies, url }) => {
    const id = url.searchParams.get('id');
    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ active: false }),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });
  },
  unlock: async ({ cookies, url }) => {
    const id = url.searchParams.get('id');
    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ lock: false }),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });
  },
  lock: async ({ cookies, url }) => {
    const id = url.searchParams.get('id');
    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ lock: true }),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });
  },
  delete: async ({ cookies, url }) => {
    const id = url.searchParams.get('id');
    const response = await makeRequest({
      method: HttpRequest.DELETE,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });
  },
  roles: async ({ cookies, url }) => {
    console.log('roles');
  },
} satisfies Actions;
