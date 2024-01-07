import { JWT_SECRET } from '$env/static/private';
import type { AuthTokensDTO } from '$lib/models/auth/auth-tokens';
import type { JwtPayload } from '$lib/models/auth/jwt-payload';
import { RoleName } from '$lib/models/user/role';
import { makeRequest } from '$lib/server/apis/api';
import {
  HttpRequest,
  removeAuth,
  setAccessTokenCookie,
  setRefreshTokenCookie,
} from '$lib/server/utils/util';
import { redirect, type Cookies, type Handle } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';

const protectedRoutes = ['/profile', '/admin', '/auth/sign-out', '/auth/sign-out-from-all-devices'];

export const handle = (async ({ event, resolve }) => {
  await tryToGetSignedInUser(event.cookies, event.locals);
  checkProtectedRoutes(event.url, event.cookies);
  const theme = event.cookies.get('theme');
  return await resolve(event, {
    transformPageChunk: ({ html }) => html.replace('data-theme=""', `data-theme="${theme}"`),
  });
}) satisfies Handle;

async function tryToGetSignedInUser(cookies: Cookies, locals: App.Locals): Promise<void> {
  try {
    const accessToken = cookies.get('accessToken') ?? '';
    const payload = jwt.verify(accessToken, JWT_SECRET) as JwtPayload;
    locals.userId = payload.iss;
  } catch (error) {
    await tryToRefreshToken(cookies, locals);
  }
}

async function tryToRefreshToken(cookies: Cookies, locals: App.Locals): Promise<void> {
  const refreshToken = cookies.get('refreshToken');
  if (refreshToken) {
    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/tokens/refresh',
      body: JSON.stringify({ refreshToken }),
    });

    if ('error' in response) {
      removeAuth(cookies, locals);
    } else {
      const { accessToken, refreshToken } = response as AuthTokensDTO;
      setAccessTokenCookie(cookies, accessToken);
      setRefreshTokenCookie(cookies, refreshToken);
      const { iss } = jwt.decode(accessToken) as JwtPayload;
      locals.userId = iss;
    }
  }
}

function checkProtectedRoutes(url: URL, cookies: Cookies): void {
  if (protectedRoutes.some((route) => url.pathname.startsWith(route))) {
    const accessToken = cookies.get('accessToken');
    if (!accessToken) redirect(302, '/');

    if (url.pathname.startsWith('/admin')) {
      const { roles } = jwt.decode(accessToken) as JwtPayload;
      if (!roles?.includes(RoleName.ADMIN)) redirect(302, '/');
    }
  }
}
