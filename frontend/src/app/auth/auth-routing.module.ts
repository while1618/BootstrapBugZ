import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConfirmRegistrationComponent } from './components/confirm-registration/confirm-registration.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { SignInComponent } from './components/sign-in/sign-in.component';
import { SignOutFromAllDevicesComponent } from './components/sign-out-from-all-devices/sign-out-from-all-devices.component';
import { SignOutComponent } from './components/sign-out/sign-out.component';
import { SignUpComponent } from './components/sign-up/sign-up.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'sign-up',
        component: SignUpComponent,
      },
      {
        path: 'confirm-registration',
        component: ConfirmRegistrationComponent,
      },
      {
        path: 'sign-in',
        component: SignInComponent,
      },
      {
        path: 'sign-out',
        component: SignOutComponent,
        canActivate: [AuthGuard],
      },
      {
        path: 'sign-out-from-all-devices',
        component: SignOutFromAllDevicesComponent,
        canActivate: [AuthGuard],
      },
      {
        path: 'forgot-password',
        component: ForgotPasswordComponent,
      },
      {
        path: 'reset-password',
        component: ResetPasswordComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuthRoutingModule {}
