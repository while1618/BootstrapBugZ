import { Component, OnInit } from '@angular/core';
import { LoginRequest, UserResponse } from '@bootstrapbugz/shared';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import * as AuthActions from '../+state/auth.actions';
import { AuthState } from '../+state/auth.reducer';

@Component({
  selector: 'bootstrapbugz-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  user$: Observable<UserResponse>;
  token$: Observable<string>;
  refreshToken$: Observable<string>;
  error$: Observable<Error>;
  loginRequest: LoginRequest = { usernameOrEmail: '', password: '' };

  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.user$ = this.store.select((store) => store.user);
    this.token$ = this.store.select((store) => store.token);
    this.refreshToken$ = this.store.select((store) => store.refreshToken);
    this.error$ = this.store.select((store) => store.error);
  }

  onLogin() {
    this.store.dispatch(AuthActions.login({ loginRequest: this.loginRequest }));
    this.loginRequest = { usernameOrEmail: '', password: '' };
  }
}
