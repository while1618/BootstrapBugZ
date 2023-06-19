import en from '$lib/i18n/en.json';
import { EMAIL_REGEX } from '$lib/regex/regex';
import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { fail, redirect } from '@sveltejs/kit';
import { z } from 'zod';
import type { Actions, PageServerLoad } from './$types';

export const load = (({ locals }) => {
  if (locals.userId) throw redirect(302, '/');
}) satisfies PageServerLoad;

const forgotPasswordSchema = z.object({
  email: z.string().regex(EMAIL_REGEX, { message: en['email.invalid'] }),
});

export const actions = {
  forgotPassword: async ({ request }) => {
    const formData = Object.fromEntries(await request.formData());
    const forgotPasswordForm = forgotPasswordSchema.safeParse(formData);
    if (!forgotPasswordForm.success)
      return fail(400, { errors: forgotPasswordForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/password/forgot',
      body: JSON.stringify(forgotPasswordForm.data),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    throw redirect(302, '/auth/sign-in');
  },
} satisfies Actions;
