import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ChangePasswordRequest, SimpleUser, UpdateUserRequest } from '@bootstrapbugz/shared';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  API_URL = 'localhost:8181/v1.0';

  constructor(private http: HttpClient) {}

  findAll() {
    return this.http.get<SimpleUser[]>(`${this.API_URL}/users`);
  }

  findByUsername(username: string) {
    return this.http.get<SimpleUser>(`${this.API_URL}/users/${username}`);
  }

  changePassword(changePasswordRequest: ChangePasswordRequest) {
    return this.http.put<void>(`${this.API_URL}/users/change-password`, changePasswordRequest);
  }

  updateUser(updateUser: UpdateUserRequest) {
    return this.http.put<SimpleUser>(`${this.API_URL}/users/update`, updateUser);
  }
}
