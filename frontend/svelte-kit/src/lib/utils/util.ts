import type { JwtPayload } from '$lib/models/jwt-payload';

export function decodeJWT(jwt: string): JwtPayload {
  return JSON.parse(Buffer.from(jwt.split('.')[1], 'base64').toString()) as JwtPayload;
}
