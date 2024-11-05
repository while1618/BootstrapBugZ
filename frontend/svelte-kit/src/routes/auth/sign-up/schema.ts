import type { Availability } from '$lib/models/shared/availability';
import { ErrorCode } from '$lib/models/shared/error-message';
import * as m from '$lib/paraglide/messages.js';
import { makeRequest } from '$lib/server/apis/api';
import { EMAIL_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from '$lib/server/regex/regex';
import { HttpRequest } from '$lib/server/utils/util';
import { z } from 'zod';

export const formSchema = z
  .object({
    username: z.string().superRefine((value, ctx) => checkUsername(value, ctx)),
    email: z.string().superRefine((value, ctx) => checkEmail(value, ctx)),
    password: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
    confirmPassword: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
  })
  .superRefine(({ password, confirmPassword }, ctx) => {
    doPasswordsMatch(password, confirmPassword, ctx);
  });

const checkUsername = async (username: string, ctx: z.RefinementCtx) => {
  if (!USERNAME_REGEX.test(username)) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: m.auth_usernameInvalid(),
      fatal: true,
    });
    return z.NEVER;
  }
  const response = await makeRequest({
    method: HttpRequest.POST,
    path: '/users/username/availability',
    body: JSON.stringify({ username }),
  });
  if ('error' in response) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: ErrorCode[response.codes[0]]
        ? m[ErrorCode[response.codes[0]]]()
        : m.API_ERROR_UNKNOWN(),
    });
  } else if (!(response as Availability).available) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: m.auth_usernameExists(),
    });
  }
};

const checkEmail = async (email: string, ctx: z.RefinementCtx) => {
  if (!EMAIL_REGEX.test(email)) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: m.auth_emailInvalid(),
      fatal: true,
    });
    return z.NEVER;
  }
  const response = await makeRequest({
    method: HttpRequest.POST,
    path: '/users/email/availability',
    body: JSON.stringify({ email }),
  });
  if ('error' in response) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: ErrorCode[response.codes[0]]
        ? m[ErrorCode[response.codes[0]]]()
        : m.API_ERROR_UNKNOWN(),
    });
  } else if (!(response as Availability).available) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: m.auth_emailExists(),
    });
  }
};

const doPasswordsMatch = (password: string, confirmPassword: string, ctx: z.RefinementCtx) => {
  if (password !== confirmPassword) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: m.auth_passwordsDoNotMatch(),
      path: ['confirmPassword'],
    });
  }
};

export type FormSchema = typeof formSchema;
