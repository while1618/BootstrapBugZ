import { RoleName } from '$lib/models/role';
import { decodeJWT } from '$lib/utils/util';
import { redirect, type Handle } from '@sveltejs/kit';

export const handle = (async ({ event, resolve }) => {
  const accessToken = event.cookies.get('accessToken');

  if (event.url.pathname.startsWith('/admin')) {
    if (!accessToken) throw redirect(303, '/');

    const { roles } = decodeJWT(accessToken);
    if (!roles?.includes(RoleName.ADMIN)) throw redirect(303, '/');
  }

  if (accessToken) {
    const { iss } = decodeJWT(accessToken);
    event.locals.userId = +iss;
  }

  return resolve(event);
}) satisfies Handle;
