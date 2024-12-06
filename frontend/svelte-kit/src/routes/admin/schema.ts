import * as m from '$lib/paraglide/messages.js';
import { EMAIL_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from '$lib/regex';
import { z, ZodIssueCode } from 'zod';

export const createSchema = z
  .object({
    username: z.string().regex(USERNAME_REGEX, { message: m.auth_usernameInvalid() }),
    email: z.string().regex(EMAIL_REGEX, { message: m.auth_emailInvalid() }),
    password: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
    confirmPassword: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
    active: z.boolean().default(true),
    lock: z.boolean().default(false),
    roleNames: z.string().array(),
  })
  .superRefine(({ password, confirmPassword }, ctx) => {
    if (password !== confirmPassword) {
      ctx.addIssue({
        code: ZodIssueCode.custom,
        path: ['confirmPassword'],
        message: m.auth_passwordsDoNotMatch(),
      });
    }
  });

export const roleSchema = z.object({
  roleNames: z.string().array(),
});

export const actionSchema = z.object({});
