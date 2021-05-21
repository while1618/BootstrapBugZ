export interface ChangePasswordRequest {
  confirmNewPassword: string;
  newPassword: string;
  oldPassword: string;
}

export interface UpdateUserRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}
