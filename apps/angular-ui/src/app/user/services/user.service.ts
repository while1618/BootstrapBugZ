import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL_USERS } from '../../shared/constants/paths';
import { User } from '../models/user.models';
import { ChangePasswordRequest, UpdateUserRequest } from '../models/user.requests';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  findByUsername(username: string) {
    return this.http.get<User>(`${API_URL_USERS}/${username}`);
  }

  changePassword(changePasswordRequest: ChangePasswordRequest) {
    return this.http.put<void>(`${API_URL_USERS}/change-password`, changePasswordRequest);
  }

  updateUser(updateUser: UpdateUserRequest) {
    return this.http.put<User>(`${API_URL_USERS}/update`, updateUser);
  }
}
