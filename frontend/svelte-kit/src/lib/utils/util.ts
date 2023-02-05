export function isObjectEmpty(obj: object): boolean {
  const values = Object.values(obj);
  return values.every((val) => val === null);
}

export function decodeJWT(jwt: string): object {
  return JSON.parse(Buffer.from(jwt.split('.')[1], 'base64').toString());
}
