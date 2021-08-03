import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { logout } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';

@Component({
  selector: 'bootstrapbugz-logout',
  template: '',
})
export class LogoutComponent implements OnInit {
  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.store.dispatch(logout());
  }
}
