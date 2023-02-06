import { decodeJWT } from '$lib/utils/util';
import type { Handle } from '@sveltejs/kit';

export const handle = (async ({ event, resolve }) => {
  const accessToken = event.cookies.get('accessToken');
  if (accessToken) {
    const { iss } = decodeJWT(accessToken) as { iss: number };
    event.locals.userId = iss;
  }

  return resolve(event);
}) satisfies Handle;
