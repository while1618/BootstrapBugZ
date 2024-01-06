import type { JwtPayload } from '$lib/models/auth/jwt-payload';
import { RoleName } from '$lib/models/user/role';
import type { Cookies } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';

export enum HttpRequest {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  PATCH = 'PATCH',
  DELETE = 'DELETE',
}

export function setAccessTokenCookie(cookies: Cookies, accessToken: string): void {
  const { exp } = jwt.decode(accessToken) as JwtPayload;
  cookies.set('accessToken', accessToken, {
    httpOnly: true,
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict',
    expires: new Date(exp * 1000),
  });
}

export function setRefreshTokenCookie(cookies: Cookies, refreshToken: string): void {
  const { exp } = jwt.decode(refreshToken) as JwtPayload;
  cookies.set('refreshToken', refreshToken, {
    httpOnly: true,
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict',
    expires: new Date(exp * 1000),
  });
}

export function removeAuth(cookies: Cookies, locals: App.Locals): void {
  cookies.delete('accessToken', { path: '/' });
  cookies.delete('refreshToken', { path: '/' });
  locals.userId = null;
}

export function isAdmin(accessToken: string | undefined): boolean {
  if (!accessToken) return false;
  const { roles } = jwt.decode(accessToken) as JwtPayload;
  return roles?.includes(RoleName.ADMIN) ?? false;
}
