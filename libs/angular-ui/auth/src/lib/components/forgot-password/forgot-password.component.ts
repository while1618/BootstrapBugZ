import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { forgotPassword } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';
import { ForgotPasswordRequest } from '../../models/auth.requests';

@Component({
  selector: 'bootstrapbugz-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
})
export class ForgotPasswordComponent {
  forgotPasswordForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  constructor(private store: Store<AuthState>) {}

  onSubmit() {
    if (!this.forgotPasswordForm.valid) return;

    const request = this.forgotPasswordForm.value as ForgotPasswordRequest;
    this.store.dispatch(forgotPassword({ request }));
  }
}
