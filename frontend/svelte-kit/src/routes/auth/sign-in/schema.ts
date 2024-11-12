import * as m from '$lib/paraglide/messages.js';
import { EMAIL_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from '$lib/regex';
import { z } from 'zod';

export const signInSchema = z.object({
  usernameOrEmail: z
    .string()
    .refine(
      (value) => USERNAME_REGEX.test(value) || EMAIL_REGEX.test(value),
      m.auth_usernameOrEmailInvalid(),
    ),
  password: z.string().regex(PASSWORD_REGEX, { message: m.auth_passwordInvalid() }),
});
