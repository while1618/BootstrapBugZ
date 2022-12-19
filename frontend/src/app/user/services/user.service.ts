import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL_USERS } from '../../shared/constants/paths';
import { User } from '../models/user.models';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  findAll() {
    return this.http.get<User[]>(`${API_URL_USERS}`);
  }

  findByUsername(username: string) {
    return this.http.get<User>(`${API_URL_USERS}/${username}`);
  }
}
