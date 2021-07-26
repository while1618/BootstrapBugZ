import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { signUp } from '../+state/auth.actions';
import { AuthState } from '../+state/auth.reducer';
import { SignUpRequest } from '../auth.requests';

@Component({
  selector: 'bootstrapbugz-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss'],
})
export class SignUpComponent {
  signUpForm = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    username: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    confirmPassword: new FormControl('', Validators.required),
  });

  constructor(private store: Store<AuthState>) {}

  onSubmit() {
    const request = this.signUpForm.value as SignUpRequest;
    this.store.dispatch(signUp({ request }));
  }
}
