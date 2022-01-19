import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AuthState } from '../../../auth/+state/auth.reducer';
import { getLoading, getUser } from '../../../auth/+state/auth.selectors';
import { MatchPassword } from '../../../auth/validators/match-password';
import { UniqueEmail } from '../../../auth/validators/unique-email';
import { UniqueUsername } from '../../../auth/validators/unique-username';
import { NAME_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from '../../../shared/constants/regex';
import { ChangePasswordRequest, UpdateProfileRequest } from '../../models/user.requests';

@Component({
  selector: 'bootstrapbugz-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
})
export class SettingsComponent implements OnInit {
  loading$: Observable<boolean>;
  updateProfileForm = new FormGroup(
    {
      firstName: new FormControl('', [Validators.required, Validators.pattern(NAME_REGEX)]),
      lastName: new FormControl('', [Validators.required, Validators.pattern(NAME_REGEX)]),
      username: new FormControl(
        '',
        [Validators.required, Validators.pattern(USERNAME_REGEX)],
        [this.uniqueUsername.validate]
      ),
      email: new FormControl(
        '',
        [Validators.required, Validators.email],
        [this.uniqueEmail.validate]
      ),
    },
    {
      validators: [this.matchPassword.validate],
    }
  );
  changePasswordForm = new FormGroup(
    {
      oldPassword: new FormControl('', [Validators.required, Validators.pattern(PASSWORD_REGEX)]),
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
    private uniqueUsername: UniqueUsername,
    private uniqueEmail: UniqueEmail
  ) {}

  ngOnInit(): void {
    this.loading$ = this.store.select(getLoading);
    this.store.select(getUser).subscribe((user) => {
      if (user) {
        this.updateProfileForm.setValue({
          firstName: user.firstName,
          lastName: user.lastName,
          username: user.username,
          email: user.email,
        });
      }
    });
  }

  onProfileUpdate() {
    if (!this.updateProfileForm.valid) return;

    const request = this.updateProfileForm.value as UpdateProfileRequest;
  }

  onPasswordChange() {
    if (!this.changePasswordForm.valid) return;

    const request = this.changePasswordForm.value as ChangePasswordRequest;
  }
}
