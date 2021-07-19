import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AdminRequest, ChangeRoleRequest, UserResponse } from '@bootstrapbugz/shared';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  API_URL = 'http://localhost:8181/v1.0/admin';

  constructor(private http: HttpClient) {}

  findAllUsers() {
    return this.http.get<UserResponse>(`${this.API_URL}/users`);
  }

  activate(adminRequest: AdminRequest) {
    return this.http.put<void>(`${this.API_URL}/users/activate`, adminRequest);
  }

  deactivate(adminRequest: AdminRequest) {
    return this.http.put<void>(`${this.API_URL}/users/deactivate`, adminRequest);
  }

  lock(adminRequest: AdminRequest) {
    return this.http.put<void>(`${this.API_URL}/users/lock`, adminRequest);
  }

  unlock(adminRequest: AdminRequest) {
    return this.http.put<void>(`${this.API_URL}/users/unlock`, adminRequest);
  }

  delete(adminRequest: AdminRequest) {
    return this.http.request<void>('delete', `${this.API_URL}/users/delete`, {
      body: adminRequest,
    });
  }

  changeRole(changeRoleRequest: ChangeRoleRequest) {
    return this.http.post<void>(`${this.API_URL}/users/role`, changeRoleRequest);
  }
}
