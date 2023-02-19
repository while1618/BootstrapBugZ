import { API_URL } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import type { ErrorMessage } from '$lib/models/error-message';
import {
  EMAIL_REGEX,
  FIRST_AND_LAST_NAME_REGEX,
  PASSWORD_REGEX,
  USERNAME_REGEX,
} from '$lib/regex/regex';
import { isObjectEmpty } from '$lib/utils/util';
import { fail, redirect, type Actions } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load = (({ locals }) => {
  if (!locals.user) throw redirect(302, '/');
}) satisfies PageServerLoad;

interface UpdateUserRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}

interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}

interface ProfileErrors {
  firstName: string | null;
  lastName: string | null;
  username: string | null;
  email: string | null;
  oldPassword: string | null;
  newPassword: string | null;
  confirmNewPassword: string | null;
}

export const actions = {
  update: async ({ fetch, request, cookies }) => {
    const formData = await request.formData();
    const updateUserRequest = getUpdateUserRequest(formData);
    const errors = await checkProfileErrors(updateUserRequest);
    if (!isObjectEmpty(errors)) return fail(400, { errors });

    const response = await fetch(`${API_URL}/profile/update`, {
      method: 'PUT',
      body: JSON.stringify(updateUserRequest),
      headers: {
        Authorization: cookies.get('accessToken') || '',
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 200) {
      const errorMessage = (await response.json()) as ErrorMessage;
      console.log(errorMessage);
      return fail(response.status, { errorMessage });
    }
  },
  changePassword: async ({ fetch, request, cookies, locals }) => {
    const formData = await request.formData();
    const changePasswordRequest = getChangePasswordRequest(formData);
    const errors = await checkProfileErrors(changePasswordRequest);
    if (!isObjectEmpty(errors)) return fail(400, { errors });

    console.log(JSON.stringify(changePasswordRequest));

    const response = await fetch(`${API_URL}/profile/change-password`, {
      method: 'PUT',
      body: JSON.stringify(changePasswordRequest),
      headers: {
        Authorization: cookies.get('accessToken') || '',
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 204) {
      const errorMessage = (await response.json()) as ErrorMessage;
      console.log(errorMessage);
      return fail(response.status, { errorMessage });
    }

    cookies.delete('accessToken', { path: '/' });
    cookies.delete('refreshToken', { path: '/' });
    locals.user = null;

    throw redirect(303, '/');
  },
} satisfies Actions;

const getUpdateUserRequest = (request: FormData): UpdateUserRequest => {
  return {
    firstName: request.get('firstName'),
    lastName: request.get('lastName'),
    username: request.get('username'),
    email: request.get('email'),
  } as UpdateUserRequest;
};

const getChangePasswordRequest = (request: FormData): ChangePasswordRequest => {
  return {
    oldPassword: request.get('oldPassword'),
    newPassword: request.get('newPassword'),
    confirmNewPassword: request.get('confirmNewPassword'),
  } as ChangePasswordRequest;
};

const checkProfileErrors = async (
  request: UpdateUserRequest | ChangePasswordRequest
): Promise<ProfileErrors> => {
  const errors: ProfileErrors = {
    firstName: null,
    lastName: null,
    username: null,
    email: null,
    oldPassword: null,
    newPassword: null,
    confirmNewPassword: null,
  };

  if ('firstName' in request && !FIRST_AND_LAST_NAME_REGEX.test(request.firstName))
    errors.firstName = en['firstName.invalid'];
  if ('lastName' in request && !FIRST_AND_LAST_NAME_REGEX.test(request.lastName))
    errors.lastName = en['lastName.invalid'];

  if ('username' in request && !USERNAME_REGEX.test(request.username))
    errors.username = en['username.invalid'];

  if ('email' in request && request.email === '') errors.email = en['email.invalid'];
  if ('email' in request && !EMAIL_REGEX.test(request.email)) errors.email = en['email.invalid'];

  if ('newPassword' in request && !PASSWORD_REGEX.test(request.newPassword))
    errors.newPassword = en['password.invalid'];
  if ('confirmNewPassword' in request && !PASSWORD_REGEX.test(request.confirmNewPassword))
    errors.confirmNewPassword = en['password.invalid'];
  if (
    'newPassword' in request &&
    'confirmNewPassword' in request &&
    request.newPassword !== request.confirmNewPassword
  )
    errors.confirmNewPassword = en['password.doNotMatch'];

  return errors;
};
