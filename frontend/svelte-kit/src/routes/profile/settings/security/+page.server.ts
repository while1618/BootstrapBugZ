import { makeRequest } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import { PASSWORD_REGEX } from '$lib/regex/regex';
import { HttpRequest, removeAuth } from '$lib/utils/util';
import { fail, redirect, type Actions } from '@sveltejs/kit';
import { z } from 'zod';

const changePasswordSchema = z
  .object({
    oldPassword: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
    newPassword: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
    confirmNewPassword: z.string().regex(PASSWORD_REGEX, { message: en['password.invalid'] }),
  })
  .superRefine(({ newPassword, confirmNewPassword }, ctx) => {
    if (newPassword !== confirmNewPassword) {
      ctx.addIssue({
        code: 'custom',
        message: en['password.doNotMatch'],
        path: ['confirmNewPassword'],
      });
    }
  });

export const actions = {
  changePassword: async ({ request, cookies, locals }) => {
    const formData = Object.fromEntries(await request.formData());
    const changePasswordForm = changePasswordSchema.safeParse(formData);
    if (!changePasswordForm.success)
      return fail(400, { errors: changePasswordForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.PUT,
      path: '/profile/change-password',
      body: JSON.stringify(changePasswordForm.data),
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });

    removeAuth(cookies, locals);

    throw redirect(302, '/');
  },
} satisfies Actions;
