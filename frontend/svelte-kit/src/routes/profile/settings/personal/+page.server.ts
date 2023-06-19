import en from '$lib/i18n/en.json';
import { EMAIL_REGEX, FIRST_AND_LAST_NAME_REGEX, USERNAME_REGEX } from '$lib/regex/regex';
import { makeRequest } from '$lib/server/apis/api';
import { HttpRequest } from '$lib/server/utils/util';
import { fail, type Actions } from '@sveltejs/kit';
import { z } from 'zod';

const updateProfileSchema = z.object({
  firstName: z.string().regex(FIRST_AND_LAST_NAME_REGEX, { message: en['firstName.invalid'] }),
  lastName: z.string().regex(FIRST_AND_LAST_NAME_REGEX, { message: en['lastName.invalid'] }),
  username: z.string().regex(USERNAME_REGEX, { message: en['username.invalid'] }),
  email: z.string().regex(EMAIL_REGEX, { message: en['email.invalid'] }),
});

export const actions = {
  updateProfile: async ({ request, cookies }) => {
    const formData = Object.fromEntries(await request.formData());
    const updateProfileForm = updateProfileSchema.safeParse(formData);
    if (!updateProfileForm.success)
      return fail(400, { errors: updateProfileForm.error.flatten().fieldErrors });

    const response = await makeRequest({
      method: HttpRequest.PATCH,
      path: '/profile',
      body: JSON.stringify(updateProfileForm.data),
      auth: cookies.get('accessToken'),
    });

    if ('error' in response) return fail(response.status, { errorMessage: response });
  },
} satisfies Actions;
