import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ResetPasswordRequest } from '@bootstrapbugz/shared';
import { Store } from '@ngrx/store';
import { resetPassword } from '../+state/auth.actions';
import { AuthState } from '../+state/auth.reducer';

@Component({
  selector: 'bootstrapbugz-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
export class ResetPasswordComponent {
  resetPasswordForm = new FormGroup({
    password: new FormControl('', [Validators.required]),
    confirmPassword: new FormControl('', [Validators.required]),
  });

  constructor(private store: Store<AuthState>, private route: ActivatedRoute) {}

  onSubmit() {
    const request: ResetPasswordRequest = {
      token: this.route.snapshot.queryParamMap.get('token'),
      password: this.resetPasswordForm.controls.password.value,
      confirmPassword: this.resetPasswordForm.controls.confirmPassword.value,
    };
    this.store.dispatch(resetPassword({ request }));
  }
}
