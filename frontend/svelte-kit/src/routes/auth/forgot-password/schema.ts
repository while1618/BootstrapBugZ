import * as m from '$lib/paraglide/messages.js';
import { EMAIL_REGEX } from '$lib/regex';
import { z } from 'zod';

export const formSchema = z.object({
  email: z.string().regex(EMAIL_REGEX, { message: m.auth_emailInvalid() }),
});

export type FormSchema = typeof formSchema;
