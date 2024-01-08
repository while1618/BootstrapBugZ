import { RoleName } from '$lib/models/user/role';
import type { UserDTO } from '$lib/models/user/user';
import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { error, fail, type Cookies, type NumericRange } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';

export const load = (async ({ cookies }) => {
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: '/admin/users',
    auth: cookies.get('accessToken'),
  });

  if ('error' in response)
    error(response.status as NumericRange<400, 599>, { message: response.error });

  return {
    users: response as UserDTO[],
    roles: [{ name: RoleName.USER }, { name: RoleName.ADMIN }],
  };
}) satisfies PageServerLoad;

const makeAction = async (cookies: Cookies, url: URL, method: HttpRequest, body?: string) => {
  const id = url.searchParams.get('id');
  const response = await makeRequest({
    method,
    path: `/admin/users/${id}`,
    auth: cookies.get('accessToken'),
    body,
  });

  if ('error' in response) return fail(response.status, { errorMessage: response });
};

export const actions = {
  activate: async ({ cookies, url }) => {
    await makeAction(cookies, url, HttpRequest.PATCH, JSON.stringify({ active: true }));
  },
  deactivate: async ({ cookies, url }) => {
    await makeAction(cookies, url, HttpRequest.PATCH, JSON.stringify({ active: false }));
  },
  unlock: async ({ cookies, url }) => {
    await makeAction(cookies, url, HttpRequest.PATCH, JSON.stringify({ lock: false }));
  },
  lock: async ({ cookies, url }) => {
    await makeAction(cookies, url, HttpRequest.PATCH, JSON.stringify({ lock: true }));
  },
  delete: async ({ cookies, url }) => {
    await makeAction(cookies, url, HttpRequest.DELETE);
  },
  roles: async ({ cookies, url, request }) => {
    const formData = Object.fromEntries(await request.formData());
    await makeAction(
      cookies,
      url,
      HttpRequest.PATCH,
      JSON.stringify({ roleNames: Object.keys(formData) }),
    );
  },
} satisfies Actions;
