import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  SimpleUser,
  ChangePasswordRequest,
  UpdateUserRequest,
} from '@bootstrapbugz/angular-ui/user/data';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  API_URL = 'localhost:8181/v1.0';

  constructor(private http: HttpClient) {}

  findAll() {
    return this.http.get<SimpleUser[]>('/users');
  }

  findByUsername(username: string) {
    return this.http.get<SimpleUser>(`/users/${username}`);
  }

  changePassword(changePasswordRequest: ChangePasswordRequest) {
    return this.http.put('/users/change-password', changePasswordRequest);
  }

  updateUser(updateUser: UpdateUserRequest) {
    return this.http.put<SimpleUser>('/users/update', updateUser);
  }
}
