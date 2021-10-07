import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { getIsAuthenticated } from '../auth/+state/auth.selectors';

@Component({
  selector: 'bootstrapbugz-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  isAuthenticated$: Observable<boolean>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.isAuthenticated$ = this.store.select(getIsAuthenticated);
  }
}
