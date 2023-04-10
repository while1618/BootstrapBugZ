import { API_URL } from '$env/static/private';
import type { ErrorMessage } from '$lib/models/error-message';

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

export async function makeRequest(params: RequestParams): Promise<object | ErrorMessage> {
  const opts: RequestInit = {};
  let headers: HeadersInit = {};

  if (params.body) {
    headers = { 'Content-Type': 'application/json' };
    opts.body = params.body;
  }

  if (params.auth) headers = { ...headers, Authorization: params.auth };

  opts.method = params.method;
  opts.headers = headers;

  const response = await fetch(`${API_URL}${params.path}`, opts);

  if (!response.ok) return (await response.json()) as ErrorMessage;

  const text = await response.text();
  return text ? JSON.parse(text) : {};
}
