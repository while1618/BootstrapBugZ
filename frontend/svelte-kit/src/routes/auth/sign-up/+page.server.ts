import { HttpRequest, makeRequest } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import {
  EMAIL_REGEX,
  FIRST_AND_LAST_NAME_REGEX,
  PASSWORD_REGEX,
  USERNAME_REGEX,
} from '$lib/regex/regex';
import { fail, redirect } from '@sveltejs/kit';
import { z } from 'zod';
import type { Actions, PageServerLoad } from './$types';

const signUpSchema = z
  .object({
    firstName: z.string().regex(FIRST_AND_LAST_NAME_REGEX, { message: en['firstName.invalid'] }),
    lastName: z.string().regex(FIRST_AND_LAST_NAME_REGEX, { message: en['lastName.invalid'] }),
    username: z
      .string()
      .regex(USERNAME_REGEX, { message: en['username.invalid'] })
      .refine(async (value) => {
        const usernameAvailability = await makeRequest({
          method: HttpRequest.GET,
          path: `/auth/username-availability?username=${value}`,
        });
        return usernameAvailability;
      }, en['username.exists']),
    email: z
      .string()
      .regex(EMAIL_REGEX, { message: en['email.invalid'] })
      .refine(async (value) => {
        const emailAvailability = await makeRequest({
          method: HttpRequest.GET,
          path: `/auth/email-availability?email=${value}`,
        });
        return emailAvailability;
      }, en['email.exists']),
    password: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
    confirmPassword: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
  })
  .superRefine(({ password, confirmPassword }, ctx) => {
    if (password !== confirmPassword) {
      ctx.addIssue({
        code: 'custom',
        message: en['password.doNotMatch'],
        path: ['confirmPassword'],
      });
    }
  });

export const load = (({ locals }) => {
  if (locals.userId) throw redirect(302, '/');
}) satisfies PageServerLoad;

export const actions = {
  signUp: async ({ request }) => {
    const formData = Object.fromEntries(await request.formData());
    const signUpForm = await signUpSchema.safeParseAsync(formData);
    if (!signUpForm.success) return fail(400, { errors: signUpForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/sign-up',
      body: JSON.stringify(signUpForm.data),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    throw redirect(303, '/auth/sign-in');
  },
} satisfies Actions;
