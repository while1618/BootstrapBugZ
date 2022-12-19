import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL_ADMIN } from '../../shared/constants/paths';
import { AdminRequest, UpdateRoleRequest } from '../models/admin.requests';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  constructor(private http: HttpClient) {}

  activate(adminRequest: AdminRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/activate`, adminRequest);
  }

  deactivate(adminRequest: AdminRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/deactivate`, adminRequest);
  }

  unlock(adminRequest: AdminRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/unlock`, adminRequest);
  }

  lock(adminRequest: AdminRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/lock`, adminRequest);
  }

  updateRole(updateRoleRequest: UpdateRoleRequest) {
    return this.http.put<void>(`${API_URL_ADMIN}/users/update-role`, updateRoleRequest);
  }

  delete(adminRequest: AdminRequest) {
    return this.http.request<void>('delete', `${API_URL_ADMIN}/users/delete`, {
      body: adminRequest,
    });
  }
}
