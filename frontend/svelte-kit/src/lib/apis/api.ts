export const API_URL = 'http://localhost:18080/v1';

export enum HttpRequest {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  DELETE = 'DELETE',
}

export interface RequestParams {
  method: HttpRequest;
  path: string;
  body?: string;
  auth?: string;
}

export function makeRequest(params: RequestParams): Promise<Response> {
  let headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  if (params.auth) headers = { ...headers, Authorization: params.auth };

  const config: RequestInit = {
    method: params.method,
    body: params.body,
    headers,
  };
  return fetch(`${API_URL}${params.path}`, config);
}
