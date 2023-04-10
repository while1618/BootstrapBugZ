import { HttpRequest, makeRequest } from '$lib/apis/api';
import type { UserDTO } from '$lib/models/user';
import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load = (async ({ params, cookies }) => {
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: `/users/${params.name}`,
    auth: cookies.get('accessToken'),
  });

  if ('error' in response) throw error(response.status, response.details[0].message);

  return { user: response as UserDTO };
}) satisfies PageServerLoad;
