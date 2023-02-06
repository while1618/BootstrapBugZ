import { redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load = (({ locals }) => {
  if (!locals.userId) throw redirect(302, '/');
}) satisfies PageServerLoad;
