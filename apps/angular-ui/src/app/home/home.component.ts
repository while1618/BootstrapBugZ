import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { getIsAuthenticated } from '../auth/+state/auth.selectors';
import { UserService } from '../user/services/user.service';

@Component({
  selector: 'bootstrapbugz-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  isAuthenticated$: Observable<boolean>;

  constructor(private store: Store, private userService: UserService) {}

  ngOnInit(): void {
    this.isAuthenticated$ = this.store.select(getIsAuthenticated);
  }

  onClick(): void {
    this.userService.findByUsername('user').subscribe();
  }
}
