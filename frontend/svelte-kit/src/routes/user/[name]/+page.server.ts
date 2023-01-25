import type { UserDTO } from '$lib/models/user';
import type { PageServerLoad } from './$types';

export const load = (async ({ fetch, params }) => {
  const response = await fetch(`http://localhost:8080/v1/users/${params.name}`);
  const user = (await response.json()) as UserDTO;

  return { user };
}) satisfies PageServerLoad;
