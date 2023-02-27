import { HttpRequest, makeRequest } from '$lib/apis/api';
import type { UserDTO } from '$lib/models/user';
import type { PageServerLoad } from './$types';

export const load = (async ({ params }) => {
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: `/users/${params.name}`,
  });

  if ('error' in response) return new Response(String(response.error));

  return { user: response as UserDTO };
}) satisfies PageServerLoad;
