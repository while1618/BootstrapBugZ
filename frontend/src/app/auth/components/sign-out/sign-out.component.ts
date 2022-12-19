import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { signOut } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';

@Component({
  selector: 'bootstrapbugz-sign-out',
  template: '',
})
export class SignOutComponent implements OnInit {
  constructor(private store: Store<AuthState>) {}

  ngOnInit(): void {
    this.store.dispatch(signOut());
  }
}
