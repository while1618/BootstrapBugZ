import { HttpRequest } from '@angular/common/http';

export const addAccessTokenToRequest = (
  request: HttpRequest<unknown>,
  accessToken: string
): HttpRequest<unknown> => {
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
  });
};
