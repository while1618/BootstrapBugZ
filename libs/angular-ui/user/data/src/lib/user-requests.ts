export interface ChangePasswordRequest {
  confirmNewPassword: string;
  newPassword: string;
  oldPassword: string;
}
