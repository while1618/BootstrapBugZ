import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { PASSWORD_REGEX } from '@bootstrapbugz/angular-ui/shared';
import { Store } from '@ngrx/store';
import { resetPassword } from '../+state/auth.actions';
import { AuthState } from '../+state/auth.reducer';
import { ResetPasswordRequest } from '../model/auth.requests';

@Component({
  selector: 'bootstrapbugz-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
export class ResetPasswordComponent {
  resetPasswordForm = new FormGroup({
    password: new FormControl('', [Validators.required, Validators.pattern(PASSWORD_REGEX)]),
    confirmPassword: new FormControl('', [Validators.required, Validators.pattern(PASSWORD_REGEX)]),
  });

  constructor(private store: Store<AuthState>, private route: ActivatedRoute) {}

  onSubmit() {
    if (!this.resetPasswordForm.valid) return;

    const request: ResetPasswordRequest = {
      token: this.route.snapshot.queryParamMap.get('token'),
      password: this.resetPasswordForm.controls.password.value,
      confirmPassword: this.resetPasswordForm.controls.confirmPassword.value,
    };
    this.store.dispatch(resetPassword({ request }));
  }
}
