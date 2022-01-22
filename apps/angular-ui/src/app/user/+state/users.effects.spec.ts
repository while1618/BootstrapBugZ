// import { TestBed } from '@angular/core/testing';
// import { provideMockActions } from '@ngrx/effects/testing';
// import { Action } from '@ngrx/store';
// import { provideMockStore } from '@ngrx/store/testing';
// import { NxModule } from '@nrwl/angular';
// import { hot } from 'jasmine-marbles';
// import { Observable } from 'rxjs';

// import * as UsersActions from './users.actions';
// import { UsersEffects } from './users.effects';

// describe('UsersEffects', () => {
//   let actions: Observable<Action>;
//   let effects: UsersEffects;

//   beforeEach(() => {
//     TestBed.configureTestingModule({
//       imports: [NxModule.forRoot()],
//       providers: [UsersEffects, provideMockActions(() => actions), provideMockStore()],
//     });

//     effects = TestBed.inject(UsersEffects);
//   });

//   describe('init$', () => {
//     it('should work', () => {
//       actions = hot('-a-|', { a: UsersActions.init() });

//       const expected = hot('-a-|', { a: UsersActions.loadUsersSuccess({ users: [] }) });

//       expect(effects.init$).toBeObservable(expected);
//     });
//   });
// });
