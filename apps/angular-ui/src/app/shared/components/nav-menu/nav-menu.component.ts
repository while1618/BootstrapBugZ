import { Component, DoCheck } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AuthState } from '../../../auth/+state/auth.reducer';
import { getIsAuthenticated } from '../../../auth/+state/auth.selectors';

@Component({
  selector: 'bootstrapbugz-nav-menu',
  templateUrl: './nav-menu.component.html',
  styleUrls: ['./nav-menu.component.scss'],
})
export class NavMenuComponent implements DoCheck {
  isAuthenticated$: Observable<boolean>;

  constructor(private store: Store<AuthState>) {}

  ngDoCheck() {
    this.isAuthenticated$ = this.store.pipe(select(getIsAuthenticated));
  }
}
