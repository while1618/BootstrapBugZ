import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { LoaderComponent } from './components/loader/loader.component';
import { NavMenuComponent } from './components/nav-menu/nav-menu.component';

@NgModule({
  declarations: [NavMenuComponent, LoaderComponent],
  imports: [CommonModule, MatToolbarModule, MatButtonModule, MatIconModule, RouterModule],
  exports: [NavMenuComponent, LoaderComponent],
})
export class SharedModule {}
