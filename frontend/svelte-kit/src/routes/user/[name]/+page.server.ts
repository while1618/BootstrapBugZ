import { HttpRequest, makeRequest } from '$lib/apis/api';
import type { UserDTO } from '$lib/models/user';
import type { PageServerLoad } from './$types';

export const load = (async ({ params }) => {
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: `/users/${params.name}`,
  });
  const user = (await response.json()) as UserDTO;

  return { user };
}) satisfies PageServerLoad;
