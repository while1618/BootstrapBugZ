import type { PageServerLoad } from './$types';

export const load = (async ({ parent }) => {
  const { profile } = await parent();
  return { profile };
}) satisfies PageServerLoad;
