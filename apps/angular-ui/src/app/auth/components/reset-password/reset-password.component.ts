import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { resetPassword } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';
import { PASSWORD_REGEX } from '../../../shared/constants/regex';
import { ResetPasswordRequest } from '../../models/auth.requests';
import { MatchPassword } from '../../validators/match-password';

@Component({
  selector: 'bootstrapbugz-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
export class ResetPasswordComponent {
  resetPasswordForm = new FormGroup(
    {
      password: new FormControl('', [Validators.required, Validators.pattern(PASSWORD_REGEX)]),
      confirmPassword: new FormControl('', [
        Validators.required,
        Validators.pattern(PASSWORD_REGEX),
      ]),
    },
    { validators: [this.matchPassword.validate] }
  );

  constructor(
    private store: Store<AuthState>,
    private route: ActivatedRoute,
    private matchPassword: MatchPassword
  ) {}

  onSubmit() {
    if (!this.resetPasswordForm.valid) return;

    const request: ResetPasswordRequest = {
      accessToken: this.route.snapshot.queryParamMap.get('accessToken'),
      password: this.resetPasswordForm.controls.password.value,
      confirmPassword: this.resetPasswordForm.controls.confirmPassword.value,
    };
    this.store.dispatch(resetPassword({ request }));
  }
}
