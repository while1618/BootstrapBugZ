import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { resendConfirmationEmail, signIn } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';
import { getLoading } from '../../+state/auth.selectors';
import { ResendConfirmationEmailRequest, SignInRequest } from '../../models/auth.requests';

@Component({
  selector: 'bootstrapbugz-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss'],
})
export class SignInComponent implements OnInit {
  loading$: Observable<boolean>;
  disableResendButton = false;
  signInForm = new FormGroup({
    usernameOrEmail: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });
  private resendCounter = 0;

  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.loading$ = this.store.select(getLoading);
  }

  onSubmit() {
    if (!this.signInForm.valid) return;

    const request = this.signInForm.value as SignInRequest;
    this.store.dispatch(signIn({ request }));
  }

  resendConfirmationEmail() {
    this.resendCounter++;
    if (this.resendCounter >= 3) this.disableResendButton = true;
    const request: ResendConfirmationEmailRequest = {
      usernameOrEmail: this.signInForm.controls.usernameOrEmail.value,
    };
    this.store.dispatch(resendConfirmationEmail({ request }));
  }
}
