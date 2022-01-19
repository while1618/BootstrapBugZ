import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../auth/guards/auth.guard';
import { SettingsComponent } from './components/settings/settings.component';
import { UserComponent } from './components/user/user.component';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: ':username',
        component: UserComponent,
      },
      {
        path: 'settings/profile',
        component: SettingsComponent,
        canActivate: [AuthGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserRoutingModule {}
