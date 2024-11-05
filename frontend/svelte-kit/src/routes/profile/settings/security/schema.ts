import * as m from '$lib/paraglide/messages.js';
import { PASSWORD_REGEX } from '$lib/regex';
import { z } from 'zod';

export const formSchema = z
  .object({
    currentPassword: z.string().regex(PASSWORD_REGEX, { message: m.profile_passwordInvalid() }),
    newPassword: z.string().regex(PASSWORD_REGEX, { message: m.profile_passwordInvalid() }),
    confirmNewPassword: z.string().regex(PASSWORD_REGEX, { message: m.profile_passwordInvalid() }),
  })
  .superRefine(({ newPassword, confirmNewPassword }, ctx) => {
    if (newPassword !== confirmNewPassword) {
      ctx.addIssue({
        code: 'custom',
        message: m.profile_passwordsDoNotMatch(),
        path: ['confirmNewPassword'],
      });
    }
  });

export type FormSchema = typeof formSchema;
