import { Component, OnInit } from '@angular/core';
import { SignUpRequest, UserResponse } from '@bootstrapbugz/shared';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import * as AuthActions from '../+state/auth.actions';
import { AuthState } from '../+state/auth.reducer';

@Component({
  selector: 'bootstrapbugz-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss'],
})
export class SignUpComponent implements OnInit {
  user$: Observable<UserResponse>;
  error$: Observable<Error>;
  signUpRequest: SignUpRequest = {
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  };

  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.user$ = this.store.select((store) => store.user);
    this.error$ = this.store.select((store) => store.error);
  }

  onSignUp() {
    this.store.dispatch(AuthActions.signUp({ signUpRequest: this.signUpRequest }));
    this.signUpRequest = {
      firstName: '',
      lastName: '',
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
    };
  }
}
