import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConfirmRegistrationComponent } from './components/confirm-registration/confirm-registration.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { LoginComponent } from './components/login/login.component';
import { LogoutFromAllDevicesComponent } from './components/logout-from-all-devices/logout-from-all-devices.component';
import { LogoutComponent } from './components/logout/logout.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { SignUpComponent } from './components/sign-up/sign-up.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: 'logout',
        component: LogoutComponent,
        canActivate: [AuthGuard],
      },
      {
        path: 'logout-from-all-devices',
        component: LogoutFromAllDevicesComponent,
        canActivate: [AuthGuard],
      },
      {
        path: 'sign-up',
        component: SignUpComponent,
      },
      {
        path: 'confirm-registration',
        component: ConfirmRegistrationComponent,
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
