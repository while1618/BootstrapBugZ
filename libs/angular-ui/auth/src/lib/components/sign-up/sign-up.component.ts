import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  MatchPassword,
  NAME_REGEX,
  PASSWORD_REGEX,
  USERNAME_REGEX,
} from '@bootstrapbugz/angular-ui/shared';
import { Store } from '@ngrx/store';
import { signUp } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';
import { SignUpRequest } from '../../models/auth.requests';
import { UniqueUsername } from '../../validators/unique-username';

@Component({
  selector: 'bootstrapbugz-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss'],
})
export class SignUpComponent {
  signUpForm = new FormGroup(
    {
      firstName: new FormControl('', [Validators.required, Validators.pattern(NAME_REGEX)]),
      lastName: new FormControl('', [Validators.required, Validators.pattern(NAME_REGEX)]),
      username: new FormControl(
        '',
        [Validators.required, Validators.pattern(USERNAME_REGEX)],
        [this.uniqueUsername.validate]
      ),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.pattern(PASSWORD_REGEX)]),
      confirmPassword: new FormControl('', [
        Validators.required,
        Validators.pattern(PASSWORD_REGEX),
      ]),
    },
    {
      validators: [this.matchPassword.validate],
    }
  );

  constructor(
    private store: Store<AuthState>,
    private matchPassword: MatchPassword,
    private uniqueUsername: UniqueUsername
  ) {}

  onSubmit() {
    if (!this.signUpForm.valid) return;

    const request = this.signUpForm.value as SignUpRequest;
    this.store.dispatch(signUp({ request }));
  }
}
