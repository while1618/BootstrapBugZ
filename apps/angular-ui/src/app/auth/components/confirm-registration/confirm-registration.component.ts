import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { confirmRegistration } from '../../+state/auth.actions';
import { AuthState } from '../../+state/auth.reducer';

@Component({
  selector: 'bootstrapbugz-confirm-registration',
  template: '',
})
export class ConfirmRegistrationComponent implements OnInit {
  constructor(private store: Store<AuthState>, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    this.store.dispatch(confirmRegistration({ request: token }));
  }
}
