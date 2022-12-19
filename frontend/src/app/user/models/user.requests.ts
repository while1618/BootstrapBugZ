export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
}

export interface ChangePasswordRequest {
  confirmNewPassword: string;
  newPassword: string;
  oldPassword: string;
}
