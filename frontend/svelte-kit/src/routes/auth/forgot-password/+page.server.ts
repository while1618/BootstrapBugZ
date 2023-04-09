import { HttpRequest, makeRequest } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import { EMAIL_REGEX } from '$lib/regex/regex';
import { fail, redirect } from '@sveltejs/kit';
import { z } from 'zod';
import type { Actions, PageServerLoad } from './$types';

const forgotPasswordSchema = z.object({
  email: z.string().regex(EMAIL_REGEX, { message: en['email.invalid'] }),
});

export const load = (({ locals }) => {
  if (locals.userId) throw redirect(302, '/');
}) satisfies PageServerLoad;

export const actions = {
  forgotPassword: async ({ request }) => {
    const formData = Object.fromEntries(await request.formData());
    const forgotPasswordForm = forgotPasswordSchema.safeParse(formData);
    if (!forgotPasswordForm.success)
      return fail(400, { errors: forgotPasswordForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.POST,
      path: '/auth/forgot-password',
      body: JSON.stringify(forgotPasswordForm.data),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    throw redirect(303, '/auth/sign-in');
  },
} satisfies Actions;
