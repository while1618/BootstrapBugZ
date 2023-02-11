import { API_URL } from '$lib/apis/api';
import type { ErrorMessage } from '$lib/models/error-message';
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
    const response = await fetch(`${API_URL}/auth/signed-in-user`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: accessToken,
      },
    });

    if (response.status !== 200) {
      const errorMessage = (await response.json()) as ErrorMessage;
      console.log(errorMessage);
      return new Response(String(errorMessage.error));
    }

    event.locals.user = (await response.json()) as UserDTO;
  }

  return resolve(event);
}) satisfies Handle;
