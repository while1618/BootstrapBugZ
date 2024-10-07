import * as m from '$lib/paraglide/messages.js';
import { makeRequest } from '$lib/server/apis/api';
import { PASSWORD_REGEX } from '$lib/server/regex/regex';
import { HttpRequest, removeAuth } from '$lib/server/utils/util';
import { fail, redirect, type Actions } from '@sveltejs/kit';
import { z } from 'zod';

function createChangePasswordSchema() {
  return z
    .object({
      oldPassword: z.string().regex(PASSWORD_REGEX, { message: m.profile_invalidPassword() }),
      newPassword: z.string().regex(PASSWORD_REGEX, { message: m.profile_invalidPassword() }),
      confirmNewPassword: z
        .string()
        .regex(PASSWORD_REGEX, { message: m.profile_invalidPassword() }),
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
}

export const actions = {
  changePassword: async ({ request, cookies, locals }) => {
    const formData = Object.fromEntries(await request.formData());
    const schema = createChangePasswordSchema();
    const changePasswordForm = schema.safeParse(formData);
    if (!changePasswordForm.success)
      return fail(400, { errors: changePasswordForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: '/profile/password',
      body: JSON.stringify(changePasswordForm.data),
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    removeAuth(cookies, locals);
    redirect(302, '/');
  },
  delete: async ({ cookies, locals }) => {
    const response = await makeRequest({
      method: HttpRequest.DELETE,
      path: '/profile',
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    removeAuth(cookies, locals);
    redirect(302, '/');
  },
} satisfies Actions;
