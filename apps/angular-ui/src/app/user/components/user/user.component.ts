import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { findByUsername } from '../../+state/users.actions';
import { getLoading, getUsers } from '../../+state/users.selectors';
import { getUser } from '../../../auth/+state/auth.selectors';
import { User } from '../../models/user.models';

@Component({
  selector: 'bootstrapbugz-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  signedInUser$: Observable<User>;
  user$: Observable<User[]>;
  loading$: Observable<boolean>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.url.subscribe(() => {
      this.signedInUser$ = this.store.select(getUser);
      this.store.dispatch(findByUsername({ request: this.route.snapshot.params.username }));
      this.user$ = this.store.select(getUsers);
      this.loading$ = this.store.select(getLoading);
    });
  }
}
