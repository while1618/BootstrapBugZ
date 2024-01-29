import { BACKEND_URL } from '$env/static/private';
import type { ErrorMessage } from '$lib/models/shared/error-message';
import type { HttpRequest } from '$lib/server/utils/util';

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

  if (params.auth) headers = { ...headers, Authorization: `Bearer ${params.auth}` };

  opts.method = params.method;
  opts.headers = headers;

  const response = await fetch(`${BACKEND_URL}${params.path}`, opts);

  if (!response.ok) return (await response.json()) as ErrorMessage;

  const text = await response.text();
  return text ? JSON.parse(text) : {};
}
