// import { UsersEntity } from './users.models';
// import { usersAdapter, UsersPartialState, initialState } from './users.reducer';
// import * as UsersSelectors from './users.selectors';

// describe('Users Selectors', () => {
//   const ERROR_MSG = 'No Error Available';
//   const getUsersId = (it: UsersEntity) => it.id;
//   const createUsersEntity = (id: string, name = '') =>
//     ({
//       id,
//       name: name || `name-${id}`,
//     } as UsersEntity);

//   let state: UsersPartialState;

//   beforeEach(() => {
//     state = {
//       users: usersAdapter.setAll(
//         [
//           createUsersEntity('PRODUCT-AAA'),
//           createUsersEntity('PRODUCT-BBB'),
//           createUsersEntity('PRODUCT-CCC'),
//         ],
//         {
//           ...initialState,
//           selectedId: 'PRODUCT-BBB',
//           error: ERROR_MSG,
//           loaded: true,
//         }
//       ),
//     };
//   });

//   describe('Users Selectors', () => {
//     it('getAllUsers() should return the list of Users', () => {
//       const results = UsersSelectors.getAllUsers(state);
//       const selId = getUsersId(results[1]);

//       expect(results.length).toBe(3);
//       expect(selId).toBe('PRODUCT-BBB');
//     });

//     it('getSelected() should return the selected Entity', () => {
//       const result = UsersSelectors.getSelected(state) as UsersEntity;
//       const selId = getUsersId(result);

//       expect(selId).toBe('PRODUCT-BBB');
//     });

//     it('getUsersLoaded() should return the current "loaded" status', () => {
//       const result = UsersSelectors.getUsersLoaded(state);

//       expect(result).toBe(true);
//     });

//     it('getUsersError() should return the current "error" state', () => {
//       const result = UsersSelectors.getUsersError(state);

//       expect(result).toBe(ERROR_MSG);
//     });
//   });
// });
