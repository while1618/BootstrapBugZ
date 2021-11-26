import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { login, resendConfirmationEmail } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';
import { getLoading } from '../../+state/auth.selectors';
import { LoginRequest, ResendConfirmationEmailRequest } from '../../models/auth.requests';

@Component({
  selector: 'bootstrapbugz-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loading$: Observable<boolean>;
  disableResendButton = false;
  loginForm = new FormGroup({
    usernameOrEmail: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });
  private resendCounter = 0;

  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.loading$ = this.store.select(getLoading);
  }

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
