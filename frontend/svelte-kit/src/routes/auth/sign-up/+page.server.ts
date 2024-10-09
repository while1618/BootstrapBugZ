import type { Availability } from '$lib/models/shared/availability';
import * as m from '$lib/paraglide/messages.js';
import { makeRequest } from '$lib/server/apis/api';
import {
  EMAIL_REGEX,
  FIRST_AND_LAST_NAME_REGEX,
  PASSWORD_REGEX,
  USERNAME_REGEX,
} from '$lib/server/regex/regex';
import { HttpRequest } from '$lib/server/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import { z } from 'zod';
import type { Actions, PageServerLoad } from './$types';

export const load = (({ locals }) => {
  if (locals.userId) redirect(302, '/');
}) satisfies PageServerLoad;

function createSignUpSchema() {
  return z
    .object({
      firstName: z
        .string()
        .regex(FIRST_AND_LAST_NAME_REGEX, { message: m.auth_firstNameInvalid() }),
      lastName: z.string().regex(FIRST_AND_LAST_NAME_REGEX, { message: m.auth_lastNameInvalid() }),
      username: z
        .string()
        .regex(USERNAME_REGEX, { message: m.auth_usernameInvalid() })
        .refine(async (value) => {
          const response = await makeRequest({
            method: HttpRequest.POST,
            path: '/users/username/availability',
            body: JSON.stringify({ username: value }),
          });
          if ('error' in response) return false;
          return (response as Availability).available;
        }, m.auth_usernameExists()),
      email: z
        .string()
        .regex(EMAIL_REGEX, { message: m.auth_emailInvalid() })
        .refine(async (value) => {
          const response = await makeRequest({
            method: HttpRequest.POST,
            path: '/users/email/availability',
            body: JSON.stringify({ email: value }),
          });
          if ('error' in response) return false;
          return (response as Availability).available;
        }, m.auth_emailExists()),
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
}

export const actions = {
  signUp: async ({ request }) => {
    const formData = Object.fromEntries(await request.formData());
    const schema = createSignUpSchema();
    const signUpForm = await schema.safeParseAsync(formData);
    if (!signUpForm.success) return fail(400, { errors: signUpForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/register',
      body: JSON.stringify(signUpForm.data),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    redirect(302, '/auth/sign-in');
  },
} satisfies Actions;
