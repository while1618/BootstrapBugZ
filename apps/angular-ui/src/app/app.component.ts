import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { getSignedInUser } from './auth/+state/auth.actions';

@Component({
  selector: 'bootstrapbugz-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(getSignedInUser());
  }
}
