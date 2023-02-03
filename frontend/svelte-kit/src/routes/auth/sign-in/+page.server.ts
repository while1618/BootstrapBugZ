import { API_URL } from '$lib/apis/api';
import en from '$lib/i18n/en.json';
import type { ErrorMessage } from '$lib/models/error-message';
import type { SignInDTO } from '$lib/models/sign-in';
import { EMAIL_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from '$lib/regex/regex';
import { isObjectEmpty } from '$lib/utils/util';
import { fail, redirect, type Cookies } from '@sveltejs/kit';
import type { Actions } from './$types';

interface SignInRequest {
  usernameOrEmail: string;
  password: string;
}

interface SignInErrors {
  usernameOrEmail: string | null;
  password: string | null;
}

export const actions = {
  signIn: async ({ fetch, request, cookies }) => {
    const formData = await request.formData();
    const signInRequest = getSignInRequest(formData);
    const errors = checkSignInRequest(signInRequest);
    if (!isObjectEmpty(errors)) return fail(400, { errors });

    const response = await fetch(`${API_URL}/auth/sign-in`, {
      method: 'POST',
      body: JSON.stringify(signInRequest),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (response.status !== 200) {
      const errorMessage = (await response.json()) as ErrorMessage;
      console.log(errorMessage);
      return fail(response.status, { errorMessage });
    }

    const { accessToken, refreshToken } = (await response.json()) as SignInDTO;
    setCookies(cookies, accessToken, refreshToken);

    throw redirect(302, '/');
  },
} satisfies Actions;

const setCookies = (cookies: Cookies, accessToken: string, refreshToken: string): void => {
  cookies.set('accessToken', accessToken, {
    httpOnly: true,
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict',
    maxAge: 60 * 15, // 15mins
  });

  cookies.set('refreshToken', refreshToken, {
    httpOnly: true,
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict',
    maxAge: 60 * 60 * 24 * 7, // 7 days
  });
};

const getSignInRequest = (formData: FormData): SignInRequest => {
  return {
    usernameOrEmail: formData.get('usernameOrEmail'),
    password: formData.get('password'),
  } as SignInRequest;
};

const checkSignInRequest = (request: SignInRequest): SignInErrors => {
  const errors: SignInErrors = {
    usernameOrEmail: null,
    password: null,
  };

  if (request.usernameOrEmail === '') errors.usernameOrEmail = en['usernameOrEmail.invalid'];
  if (!USERNAME_REGEX.test(request.usernameOrEmail) && !EMAIL_REGEX.test(request.usernameOrEmail))
    errors.usernameOrEmail = en['usernameOrEmail.invalid'];

  if (!PASSWORD_REGEX.test(request.password)) errors.password = en['password.invalid'];

  return errors;
};
