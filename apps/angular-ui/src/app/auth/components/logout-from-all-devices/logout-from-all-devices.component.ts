import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { signOutFromAllDevices } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';

@Component({
  selector: 'bootstrapbugz-logout-from-all-devices',
  template: '',
})
export class LogoutFromAllDevicesComponent implements OnInit {
  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.store.dispatch(signOutFromAllDevices());
  }
}
