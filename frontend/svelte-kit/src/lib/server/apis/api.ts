import { API_URL } from '$env/static/private';
import { ErrorCode, type ErrorMessage } from '$lib/models/shared/error-message';
import * as m from '$lib/paraglide/messages.js';
import type { HttpRequest } from '$lib/server/utils/util';
import { fail } from '@sveltejs/kit';
import { type SuperValidated, setError } from 'sveltekit-superforms';

interface RequestParams {
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

  const response = await fetch(`${API_URL}${params.path}`, opts);

  if (!response.ok) return (await response.json()) as ErrorMessage;

  const text = await response.text();
  return text ? JSON.parse(text) : {};
}

export function apiErrors(
  errorMessage: ErrorMessage,
  form: SuperValidated<Record<string, unknown>>,
) {
  for (const code of errorMessage.codes) {
    const message = ErrorCode[code] ? m[ErrorCode[code]]() : m.API_ERROR_UNKNOWN();
    setError(form, message);
  }
  return fail(errorMessage.status, { form });
}
