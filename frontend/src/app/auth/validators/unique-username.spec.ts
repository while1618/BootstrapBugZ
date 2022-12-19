import { TestBed } from '@angular/core/testing';
import { AuthService } from '../services/auth.service';
import { UniqueUsername } from './unique-username';

describe('UniqueUsername', () => {
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    authService = TestBed.inject(AuthService);
  });

  it('should create an instance', () => {
    expect(new UniqueUsername(authService)).toBeTruthy();
  });
});
