import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { signOutFromAllDevices } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';

@Component({
  selector: 'bootstrapbugz-sign-out-from-all-devices',
  template: '',
})
export class SignOutFromAllDevicesComponent implements OnInit {
  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.store.dispatch(signOutFromAllDevices());
  }
}
