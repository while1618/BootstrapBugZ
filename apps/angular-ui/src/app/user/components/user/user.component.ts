import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { getUser } from '../../../auth/+state/auth.selectors';
import { User } from '../../models/user.models';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'bootstrapbugz-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  signedInUser$: Observable<User>;
  user$: Observable<User>;

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.route.url.subscribe(() => {
      this.signedInUser$ = this.store.select(getUser);
      this.user$ = this.userService.findByUsername(this.route.snapshot.params.username);
    });
  }
}
