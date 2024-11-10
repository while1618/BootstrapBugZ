import * as m from '$lib/paraglide/messages.js';
import { EMAIL_REGEX, USERNAME_REGEX } from '$lib/regex';
import { z } from 'zod';

export const updateProfileSchema = z.object({
  username: z.string().regex(USERNAME_REGEX, { message: m.profile_usernameInvalid() }),
  email: z.string().regex(EMAIL_REGEX, { message: m.profile_emailInvalid() }),
});

export type FormSchema = typeof updateProfileSchema;
