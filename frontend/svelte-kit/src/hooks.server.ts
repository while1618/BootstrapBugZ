import { HttpRequest, makeRequest } from '$lib/apis/api';
import type { JwtPayload } from '$lib/models/jwt-payload';
import type { RefreshTokenDTO } from '$lib/models/refresh-token';
import { RoleName } from '$lib/models/role';
import { removeBearerPrefix, setAccessTokenCookie, setRefreshTokenCookie } from '$lib/utils/util';
import { redirect, type Cookies, type Handle } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';

const protectedRoutes = ['/auth/sign-out', '/auth/sign-out-from-all-devices', '/user/settings'];

export const handle = (async ({ event, resolve }) => {
  try {
    const accessToken = event.cookies.get('accessToken') ?? '';
    const payload = jwt.verify(removeBearerPrefix(accessToken), 'secret') as JwtPayload;
    event.locals.userId = payload.iss;
  } catch (error) {
    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/refresh-token',
      body: JSON.stringify({ refreshToken: event.cookies.get('refreshToken') }),
    });

    if ('error' in response) {
      event.cookies.delete('accessToken', { path: '/' });
      event.cookies.delete('refreshToken', { path: '/' });
      event.locals.userId = null;
    } else {
      const { accessToken, refreshToken } = response as RefreshTokenDTO;
      setAccessTokenCookie(event.cookies, accessToken);
      setRefreshTokenCookie(event.cookies, refreshToken);
      const { iss } = jwt.decode(removeBearerPrefix(accessToken)) as JwtPayload;
      event.locals.userId = iss;
    }
  }

  isAdminRoute(event.url, event.cookies);
  isProtectedRoute(event.url, event.cookies);

  return resolve(event);
}) satisfies Handle;

function isAdminRoute(url: URL, cookies: Cookies): void {
  if (url.pathname.startsWith('/admin')) {
    const accessToken = cookies.get('accessToken');
    if (!accessToken) throw redirect(303, '/');

    const { roles } = jwt.decode(removeBearerPrefix(accessToken)) as JwtPayload;
    if (!roles?.includes(RoleName.ADMIN)) throw redirect(303, '/');
  }
}

function isProtectedRoute(url: URL, cookies: Cookies): void {
  if (protectedRoutes.includes(url.pathname)) {
    const accessToken = cookies.get('accessToken');
    if (!accessToken) throw redirect(303, '/');
  }
}
