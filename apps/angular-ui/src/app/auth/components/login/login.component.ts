import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { login, resendConfirmationEmail } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';
import { LoginRequest, ResendConfirmationEmailRequest } from '../../models/auth.requests';

@Component({
  selector: 'bootstrapbugz-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  disableResendButton = false;
  private resendCounter = 0;

  loginForm = new FormGroup({
    usernameOrEmail: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });

  constructor(private store: Store<AuthState>) {}

  onSubmit() {
    if (!this.loginForm.valid) return;

    const request = this.loginForm.value as LoginRequest;
    this.store.dispatch(login({ request }));
  }

  resendConfirmationEmail() {
    this.resendCounter++;
    if (this.resendCounter >= 3) this.disableResendButton = true;
    const request: ResendConfirmationEmailRequest = {
      usernameOrEmail: this.loginForm.controls.usernameOrEmail.value,
    };
    this.store.dispatch(resendConfirmationEmail({ request }));
  }
}
