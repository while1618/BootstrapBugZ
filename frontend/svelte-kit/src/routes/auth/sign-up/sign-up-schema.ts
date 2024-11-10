import * as m from '$lib/paraglide/messages.js';
import { EMAIL_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from '$lib/regex';
import { z, ZodIssueCode } from 'zod';

export const signUpSchema = z
  .object({
    username: z.string().regex(USERNAME_REGEX, { message: m.auth_usernameInvalid() }),
    email: z.string().regex(EMAIL_REGEX, { message: m.auth_emailInvalid() }),
    password: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
    confirmPassword: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
  })
  .superRefine(({ password, confirmPassword }, ctx) => {
    if (password !== confirmPassword) {
      ctx.addIssue({
        code: ZodIssueCode.custom,
        message: m.auth_passwordsDoNotMatch(),
      });
    }
  });

export type FormSchema = typeof signUpSchema;
