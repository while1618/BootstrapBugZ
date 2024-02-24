import type { User } from '$lib/models/user/user';
import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { error, type NumericRange } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load = (async ({ params }) => {
  const response = await makeRequest({
    method: HttpRequest.GET,
    path: `/users/username/${params.name}`,
  });

  if ('error' in response)
    error(response.status as NumericRange<400, 599>, { message: response.error });

  return { user: response as User };
}) satisfies PageServerLoad;
