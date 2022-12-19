import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL_PROFILE } from '../../shared/constants/paths';
import { User } from '../models/user.models';
import { ChangePasswordRequest, UpdateProfileRequest } from '../models/user.requests';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  constructor(private http: HttpClient) {}

  update(updateProfileRequest: UpdateProfileRequest) {
    return this.http.put<User>(`${API_URL_PROFILE}/update`, updateProfileRequest);
  }

  changePassword(changePasswordRequest: ChangePasswordRequest) {
    return this.http.put<void>(`${API_URL_PROFILE}/change-password`, changePasswordRequest);
  }
}
