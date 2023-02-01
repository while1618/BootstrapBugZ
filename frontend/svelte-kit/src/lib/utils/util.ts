export function isObjectEmpty(obj: object): boolean {
  const values = Object.values(obj);
  return values.every((val) => val === null);
}
