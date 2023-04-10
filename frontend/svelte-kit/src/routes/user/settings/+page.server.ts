import { makeRequest } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import {
  EMAIL_REGEX,
  FIRST_AND_LAST_NAME_REGEX,
  PASSWORD_REGEX,
  USERNAME_REGEX,
} from '$lib/regex/regex';
import { HttpRequest } from '$lib/utils/util';
import { fail, redirect, type Actions } from '@sveltejs/kit';
import { z } from 'zod';

const updateUserSchema = z.object({
  firstName: z.string().regex(FIRST_AND_LAST_NAME_REGEX, { message: en['firstName.invalid'] }),
  lastName: z.string().regex(FIRST_AND_LAST_NAME_REGEX, { message: en['lastName.invalid'] }),
  username: z.string().regex(USERNAME_REGEX, { message: en['username.invalid'] }),
  email: z.string().regex(EMAIL_REGEX, { message: en['email.invalid'] }),
});

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
  update: async ({ request, cookies }) => {
    const formData = Object.fromEntries(await request.formData());
    const updateUserForm = updateUserSchema.safeParse(formData);
    if (!updateUserForm.success)
      return fail(400, { errors: updateUserForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.PUT,
      path: '/profile/update',
      body: JSON.stringify(updateUserForm.data),
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return fail(response.status, { updateErrorMessage: response });
  },
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

    if ('error' in response) return fail(response.status, { changePasswordErrorMessage: response });

    cookies.delete('accessToken', { path: '/' });
    cookies.delete('refreshToken', { path: '/' });
    locals.userId = null;

    throw redirect(302, '/');
  },
} satisfies Actions;
