import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { NavMenuComponent } from './components/nav-menu/nav-menu.component';

@NgModule({
  declarations: [NavMenuComponent],
  imports: [CommonModule, MatToolbarModule, MatButtonModule, MatIconModule, RouterModule],
  exports: [NavMenuComponent],
})
export class SharedModule {}
