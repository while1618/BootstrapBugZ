export interface ErrorMessage {
  timestamp: Date;
  status: number;
  error: string;
  codes: string[];
}

export enum ErrorCode {
  API_ERROR_AUTH_INVALID = 'auth_invalid',
  API_ERROR_AUTH_TOKEN_REQUIRED = 'auth_tokenRequired',
  API_ERROR_AUTH_TOKEN_INVALID = 'auth_tokenInvalid',
  API_ERROR_USER_FIRST_NAME_REQUIRED = 'user_firstNameRequired',
  API_ERROR_USER_FIRST_NAME_INVALID = 'user_firstNameInvalid',
  API_ERROR_USER_LAST_NAME_REQUIRED = 'user_lastNameRequired',
  API_ERROR_USER_LAST_NAME_INVALID = 'user_lastNameInvalid',
  API_ERROR_USER_USERNAME_REQUIRED = 'user_usernameRequired',
  API_ERROR_USER_USERNAME_INVALID = 'user_usernameInvalid',
  API_ERROR_USER_USERNAME_EXISTS = 'user_usernameExists',
  API_ERROR_USER_USERNAME_OR_EMAIL_REQUIRED = 'user_usernameOrEmailRequired',
  API_ERROR_USER_USERNAME_OR_EMAIL_INVALID = 'user_usernameOrEmailInvalid',
  API_ERROR_USER_EMAIL_REQUIRED = 'user_emailRequired',
  API_ERROR_USER_EMAIL_INVALID = 'user_emailInvalid',
  API_ERROR_USER_EMAIL_EXISTS = 'user_emailExists',
  API_ERROR_USER_PASSWORD_REQUIRED = 'user_passwordRequired',
  API_ERROR_USER_PASSWORD_INVALID = 'user_passwordInvalid',
  API_ERROR_USER_PASSWORDS_DO_NOT_MATCH = 'user_passwordsDoNotMatch',
  API_ERROR_USER_CURRENT_PASSWORD_WRONG = 'user_currentPasswordWrong',
  API_ERROR_USER_NOT_FOUND = 'user_notFound',
  API_ERROR_USER_ACTIVE_REQUIRED = 'user_activeRequired',
  API_ERROR_USER_ACTIVE = 'user_active',
  API_ERROR_USER_NOT_ACTIVE = 'user_notActive',
  API_ERROR_USER_LOCK_REQUIRED = 'user_lockRequired',
  API_ERROR_USER_LOCK = 'user_lock',
  API_ERROR_USER_ROLES_EMPTY = 'user_rolesEmpty',
  API_ERROR_USER_ROLE_NOT_FOUND = 'user_roleNotFound',
  API_ERROR_REQUEST_PARAMETER_MISSING = 'request_parameterMissing',
  API_ERROR_REQUEST_METHOD_NOT_SUPPORTED = 'request_methodNotSupported',
  API_ERROR_REQUEST_MESSAGE_NOT_READABLE = 'request_messageNotReadable',
  API_ERROR_REQUEST_PARAMETER_TYPE_MISMATCH = 'request_parameterTypeMismatch',
  API_ERROR_INTERNAL_SERVER_ERROR = 'server_internalError',
}
