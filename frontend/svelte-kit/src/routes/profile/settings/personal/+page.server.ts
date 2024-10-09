import * as m from '$lib/paraglide/messages.js';
import { makeRequest } from '$lib/server/apis/api';
import { EMAIL_REGEX, FIRST_AND_LAST_NAME_REGEX, USERNAME_REGEX } from '$lib/server/regex/regex';
import { HttpRequest } from '$lib/server/utils/util';
import { fail, type Actions } from '@sveltejs/kit';
import { z } from 'zod';

function createUpdateProfileSchema() {
  return z.object({
    firstName: z
      .string()
      .regex(FIRST_AND_LAST_NAME_REGEX, { message: m.profile_firstNameInvalid() }),
    lastName: z.string().regex(FIRST_AND_LAST_NAME_REGEX, { message: m.profile_lastNameInvalid() }),
    username: z.string().regex(USERNAME_REGEX, { message: m.profile_usernameInvalid() }),
    email: z.string().regex(EMAIL_REGEX, { message: m.profile_emailInvalid() }),
  });
}

export const actions = {
  updateProfile: async ({ request, cookies }) => {
    const formData = Object.fromEntries(await request.formData());
    const schema = createUpdateProfileSchema();
    const updateProfileForm = schema.safeParse(formData);
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
