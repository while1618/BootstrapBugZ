import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL_ADMIN } from '../../shared/constants/paths';
import { User } from '../../user/models/user.models';
import { AdminRequest, ChangeRoleRequest } from '../models/admin.requests';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  constructor(private http: HttpClient) {}

  findAllUsers() {
    return this.http.get<User>(`${API_URL_ADMIN}/users`);
  }

  activate(adminRequest: AdminRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/activate`, adminRequest);
  }

  deactivate(adminRequest: AdminRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/deactivate`, adminRequest);
  }

  lock(adminRequest: AdminRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/lock`, adminRequest);
  }

  unlock(adminRequest: AdminRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/unlock`, adminRequest);
  }

  delete(adminRequest: AdminRequest) {
    return this.http.request<void>('delete', `${API_URL_ADMIN}/users/delete`, {
      body: adminRequest,
    });
  }

  changeRole(changeRoleRequest: ChangeRoleRequest) {
    return this.http.post<void>(`${API_URL_ADMIN}/users/role`, changeRoleRequest);
  }
}
