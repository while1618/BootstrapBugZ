import type { Pageable } from '$lib/models/shared/pageable';
import { RoleName } from '$lib/models/user/role';
import type { User } from '$lib/models/user/user';
import * as m from '$lib/paraglide/messages.js';
import { apiErrors, makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { error } from '@sveltejs/kit';
import { message, superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import type { Actions, PageServerLoad } from './$types';
import { adminSchema, roleSchema } from './schema';

export const load = (async ({ cookies, url }) => {
  let page = Number(url.searchParams.get('page')) || 1;
  let size = Number(url.searchParams.get('size')) || 10;

  if (page < 1) page = 1;
  if (size < 1) size = 10;

  const response = await makeRequest({
    method: HttpRequest.GET,
    path: `/admin/users?page=${page}&size=${size}`,
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) error(response.status, { message: response.error });

  const activateForm = await superValidate(zod(adminSchema));
  const lockForm = await superValidate(zod(adminSchema));
  const deleteForm = await superValidate(zod(adminSchema));
  const roleForm = await superValidate(zod(roleSchema));

  return {
    users: response as Pageable<User>,
    pageable: { page, size },
    roleNames: [RoleName.USER, RoleName.ADMIN],
    activateForm,
    lockForm,
    deleteForm,
    roleForm,
  };
}) satisfies PageServerLoad;

export const actions = {
  activate: async ({ request, cookies, url }) => {
    const id = url.searchParams.get('id');
    const form = await superValidate(request, zod(adminSchema));

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ active: true }),
    });

    if ('error' in response) return apiErrors(response, form);

    return message(form, m.admin_activateSuccess());
  },
  deactivate: async ({ request, cookies, url }) => {
    const id = url.searchParams.get('id');
    const form = await superValidate(request, zod(adminSchema));

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ active: false }),
    });

    if ('error' in response) return apiErrors(response, form);

    return message(form, m.admin_deactivateSuccess());
  },
  unlock: async ({ request, cookies, url }) => {
    const id = url.searchParams.get('id');
    const form = await superValidate(request, zod(adminSchema));

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ lock: false }),
    });

    if ('error' in response) return apiErrors(response, form);

    return message(form, m.admin_unlockSuccess());
  },
  lock: async ({ request, cookies, url }) => {
    const id = url.searchParams.get('id');
    const form = await superValidate(request, zod(adminSchema));

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ lock: true }),
    });

    if ('error' in response) return apiErrors(response, form);

    return message(form, m.admin_lockSuccess());
  },
  delete: async ({ request, cookies, url }) => {
    const id = url.searchParams.get('id');
    const form = await superValidate(request, zod(adminSchema));

    const response = await makeRequest({
      method: HttpRequest.DELETE,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return apiErrors(response, form);

    return message(form, m.admin_deleteUserSuccess());
  },
  roles: async ({ request, cookies, url }) => {
    const id = url.searchParams.get('id');
    const form = await superValidate(request, zod(roleSchema));

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: `/admin/users/${id}`,
      auth: cookies.get('accessToken'),
      body: JSON.stringify({ roleNames: form.data.names }),
    });

    if ('error' in response) return apiErrors(response, form);

    return message(form, m.admin_rolesSuccess());
  },
} satisfies Actions;
