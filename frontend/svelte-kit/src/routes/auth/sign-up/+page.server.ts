import { API_URL } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import type { ErrorMessage } from '$lib/models/error-message';
import {
  EMAIL_REGEX,
  FIRST_AND_LAST_NAME_REGEX,
  PASSWORD_REGEX,
  USERNAME_REGEX,
} from '$lib/regex/regex';
import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

interface SignUpRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

interface SignUpErrors {
  firstName: string | null;
  lastName: string | null;
  username: string | null;
  email: string | null;
  password: string | null;
  confirmPassword: string | null;
}

export const actions = {
  signUp: async ({ fetch, request }) => {
    const formData = await request.formData();
    const signUpRequest = getSignUpRequest(formData);
    const errors = await checkSignUpRequest(signUpRequest);
    if (!isObjectEmpty(errors)) return fail(400, { errors });

    const response = await fetch(`${API_URL}/auth/sign-up`, {
      method: 'POST',
      body: JSON.stringify(signUpRequest),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 201) {
      const errorMessage = (await response.json()) as ErrorMessage;
      console.log(errorMessage);
      return fail(response.status, { errorMessage });
    }

    throw redirect(302, '/auth/sign-in');
  },
} satisfies Actions;

const getSignUpRequest = (request: FormData): SignUpRequest => {
  return {
    firstName: request.get('firstName'),
    lastName: request.get('lastName'),
    username: request.get('username'),
    email: request.get('email'),
    password: request.get('password'),
    confirmPassword: request.get('confirmPassword'),
  } as SignUpRequest;
};

const checkSignUpRequest = async (request: SignUpRequest): Promise<SignUpErrors> => {
  const errors: SignUpErrors = {
    firstName: null,
    lastName: null,
    username: null,
    email: null,
    password: null,
    confirmPassword: null,
  };

  if (!FIRST_AND_LAST_NAME_REGEX.test(request.firstName))
    errors.firstName = en['firstName.invalid'];
  if (!FIRST_AND_LAST_NAME_REGEX.test(request.lastName)) errors.lastName = en['lastName.invalid'];

  if (!USERNAME_REGEX.test(request.username)) errors.username = en['username.invalid'];
  const usernameAvailabilityResponse = await fetch(
    `${API_URL}/auth/username-availability?username=${request.username}`
  );
  const usernameAvailable = (await usernameAvailabilityResponse.json()) as boolean;
  if (!usernameAvailable) errors.username = en['username.exists'];

  if (request.email === '') errors.email = en['email.invalid'];
  if (!EMAIL_REGEX.test(request.email)) errors.email = en['email.invalid'];
  const emailAvailabilityResponse = await fetch(
    `${API_URL}/auth/email-availability?email=${request.email}`
  );
  const emailAvailable = (await emailAvailabilityResponse.json()) as boolean;
  if (!emailAvailable) errors.email = en['email.exists'];

  if (!PASSWORD_REGEX.test(request.password)) errors.password = en['password.invalid'];
  if (!PASSWORD_REGEX.test(request.confirmPassword))
    errors.confirmPassword = en['password.invalid'];
  if (request.password !== request.confirmPassword)
    errors.confirmPassword = en['password.doNotMatch'];

  return errors;
};

const isObjectEmpty = (obj: object): boolean => {
  const values = Object.values(obj);
  return values.every((val) => val === null);
};
