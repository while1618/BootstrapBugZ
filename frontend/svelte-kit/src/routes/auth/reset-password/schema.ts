import { JWT_SECRET } from '$env/static/private';
import * as m from '$lib/paraglide/messages.js';
import { PASSWORD_REGEX } from '$lib/regex';
import jwt from 'jsonwebtoken';
import { z } from 'zod';

export const formSchema = z
  .object({
    token: z.string().refine((token) => {
      try {
        jwt.verify(token, JWT_SECRET);
        return true;
      } catch (_) {
        return false;
      }
    }, m.auth_tokenInvalid()),
    password: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
    confirmPassword: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
  })
  .superRefine(({ password, confirmPassword }, ctx) => {
    if (password !== confirmPassword) {
      ctx.addIssue({
        code: 'custom',
        message: m.auth_passwordsDoNotMatch(),
        path: ['confirmPassword'],
      });
    }
  });

export type FormSchema = typeof formSchema;
