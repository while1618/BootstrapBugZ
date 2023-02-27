import { HttpRequest, makeRequest } from '$lib/apis/api';
import { RoleName } from '$lib/models/role';
import type { UserDTO } from '$lib/models/user';
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
    const response = await makeRequest({
      method: HttpRequest.GET,
      path: '/auth/signed-in-user',
      auth: accessToken,
    });

    if ('error' in response) return new Response(String(response.error));

    event.locals.user = response as UserDTO;
  }

  return resolve(event);
}) satisfies Handle;
