import type { JwtPayload } from '$lib/models/jwt-payload';
import type { Cookies } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';

export function removeBearerPrefix(jwt: string): string {
  return jwt.replace('Bearer ', '');
}

export function setAccessTokenCookie(cookies: Cookies, accessToken: string): void {
  const { exp } = jwt.decode(removeBearerPrefix(accessToken)) as JwtPayload;
  cookies.set('accessToken', accessToken, {
    httpOnly: true,
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict',
    expires: new Date(exp * 1000),
  });
}

export function setRefreshTokenCookie(cookies: Cookies, refreshToken: string): void {
  const { exp } = jwt.decode(removeBearerPrefix(refreshToken)) as JwtPayload;
  cookies.set('refreshToken', refreshToken, {
    httpOnly: true,
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict',
    expires: new Date(exp * 1000),
  });
}
