import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CustomErrorStateMatcher } from '@bootstrapbugz/angular-ui/shared';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { AuthEffects } from './+state/auth.effects';
import * as fromAuth from './+state/auth.reducer';
import { AngularUiAuthRoutingModule } from './angular-ui-auth-routing.module';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { LoginComponent } from './components/login/login.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { SignUpComponent } from './components/sign-up/sign-up.component';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(fromAuth.AUTH_FEATURE_KEY, fromAuth.reducer),
    EffectsModule.forFeature([AuthEffects]),
    ReactiveFormsModule,
    AngularUiAuthRoutingModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  declarations: [LoginComponent, SignUpComponent, ForgotPasswordComponent, ResetPasswordComponent],
  exports: [AngularUiAuthRoutingModule],
  providers: [
    {
      provide: ErrorStateMatcher,
      useClass: CustomErrorStateMatcher,
    },
  ],
})
export class AngularUiAuthModule {}
