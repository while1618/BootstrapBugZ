import { API_URL } from '$lib/apis/api';
import type { UserDTO } from '$lib/models/user';
import type { PageServerLoad } from './$types';

export const load = (async ({ fetch, params }) => {
  const response = await fetch(`${API_URL}/users/${params.name}`);
  const user = (await response.json()) as UserDTO;

  return { user };
}) satisfies PageServerLoad;
