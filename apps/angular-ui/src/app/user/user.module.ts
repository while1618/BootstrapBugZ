import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { UserComponent } from './components/user/user.component';
import { UserRoutingModule } from './user-routing.module';

@NgModule({
  declarations: [UserComponent],
  imports: [CommonModule, UserRoutingModule],
})
export class UserModule {}
