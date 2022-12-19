import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AuthState } from '../../../auth/+state/auth.reducer';
import { getIsAuthenticated, getSignedInUser } from '../../../auth/+state/auth.selectors';
import { User } from '../../../user/models/user.models';

@Component({
  selector: 'bootstrapbugz-nav-menu',
  templateUrl: './nav-menu.component.html',
  styleUrls: ['./nav-menu.component.scss'],
})
export class NavMenuComponent implements OnInit {
  isAuthenticated$: Observable<boolean>;
  user$: Observable<User>;

  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.isAuthenticated$ = this.store.select(getIsAuthenticated);
    this.user$ = this.store.select(getSignedInUser);
  }
}
